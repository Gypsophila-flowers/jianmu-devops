/**
 * 工作流拖拽模块
 * 
 * 该模块负责处理节点从左侧面板拖拽到画布的功能。
 * 主要功能包括：
 * - 从节点面板拖拽节点到画布
 * - 拖拽过程中的样式调整（增加节点高度以显示文字）
 * - 节点放置验证（检查放置位置是否有效）
 * - 异步任务节点参数的自动加载
 * 
 * 使用AntV X6的Addon.Dnd实现拖拽功能
 */

import { Addon, Graph, Node, Point } from '@antv/x6';
// @ts-ignore
import listen from 'good-listener';
import { IWorkflowNode } from './data/common';
import { NODE, PORTS } from '../shape/gengral-config';
import { ClickNodeWarningCallbackFnType, WorkflowValidator } from './workflow-validator';
import { CustomX6NodeProxy } from './data/custom-x6-node-proxy';
import { NodeGroupEnum, NodeTypeEnum } from './data/enumeration';
import {
  getLocalNodeParams,
  getLocalVersionList,
  getOfficialNodeParams,
  getOfficialVersionList,
} from '@/api/node-library';
import { AsyncTask } from './data/node/async-task';
import { pushParams } from './workflow-node';

/** 从配置中获取节点尺寸和文字最大高度 */
const { icon: { width, height }, textMaxHeight } = NODE;

/**
 * 拖拽监听器接口
 * @description 用于跟踪鼠标位置和事件监听器
 */
interface IDraggingListener {
  /** 鼠标在文档中的当前位置 */
  mousePosition: Point.PointLike;
  /** 鼠标移动事件监听器，用于更新鼠标位置 */
  listener?: any;
}

/**
 * WorkflowDnd 类 - 工作流拖拽管理器
 * 
 * 负责处理从节点面板到画布的拖拽操作，包括：
 * - 创建拖拽源节点和目标节点
 * - 验证节点放置的有效性
 * - 自动加载异步任务节点的参数信息
 * - 在放置时添加节点警告（如果校验失败）
 */
export class WorkflowDnd {
  /**
   * X6图形实例，用于创建节点和管理画布
   */
  private readonly graph: Graph;
  
  /**
   * X6拖拽插件实例
   */
  private readonly dnd: Addon.Dnd;
  
  /**
   * 拖拽监听器对象，用于跟踪鼠标位置
   */
  private readonly draggingListener: IDraggingListener = {
    mousePosition: { x: -1, y: -1 },
  }

  /**
   * 构造函数
   * 
   * 初始化拖拽管理器，配置拖拽行为：
   * - getDragNode：开始拖拽时的节点处理
   * - getDropNode：结束拖拽时的节点处理
   * - validateNode：验证节点是否可以放置
   * 
   * @param graph - X6图形实例
   * @param workflowValidator - 工作流校验器，用于验证放置的节点
   * @param nodeContainer - 节点面板的DOM容器
   * @param clickNodeWarningCallback - 点击节点警告图标的回调函数
   */
  constructor(graph: Graph,
    workflowValidator: WorkflowValidator,
    nodeContainer: HTMLElement,
    clickNodeWarningCallback: ClickNodeWarningCallbackFnType) {
    this.graph = graph;
    
    // 创建拖拽插件实例
    this.dnd = new Addon.Dnd({
      target: graph,                    // 目标画布
      animation: true,                   // 启用拖拽动画
      
      /**
       * 获取拖拽时的节点
       * 
       * 在开始拖拽时调用，调整节点的显示样式。
       * 拖拽时节点会临时增加高度，以便显示节点名称。
       * 
       * @param sourceNode - 源节点
       * @returns 返回处理后的节点（这里直接返回，不需要克隆）
       */
      getDragNode: (sourceNode: Node) => {
        const { width, height } = sourceNode.getSize();
        // 增加节点高度以显示节点名称
        sourceNode.resize(width, height + textMaxHeight);
        // 开始拖拽时初始化的节点，直接使用，无需克隆
        return sourceNode;
      },
      
      /**
       * 获取放置时的节点
       * 
       * 在拖拽结束、节点即将放置到画布时调用。
       * 需要将节点高度恢复，并克隆一个新节点用于放置。
       * 
       * @param draggingNode - 正在拖拽的节点
       * @returns 返回克隆后的目标节点
       */
      getDropNode: (draggingNode: Node) => {
        const { width, height } = draggingNode.getSize();
        // 恢复节点原始高度
        draggingNode.resize(width, height - textMaxHeight);

        // 结束拖拽时，必须克隆拖动的节点，因为拖动的节点和目标节点不在一个画布
        const targetNode = draggingNode.clone();
        
        // 保证不偏移：由于克隆时位置可能已经改变，需要调整位置
        setTimeout(() => {
          const { x, y } = targetNode.getPosition();
          targetNode.setPosition(x, y - textMaxHeight / 2);
        });
        return targetNode;
      },
      
      /**
       * 验证节点是否可以放置
       * 
       * 在节点放置到画布前调用，用于检查：
       * 1. 节点放置位置是否有效（不在节点面板区域内）
       * 2. 触发器数量是否超过限制
       * 3. 对于异步任务节点，自动加载其参数信息
       * 4. 对节点进行参数校验，必要时添加警告图标
       * 
       * @param droppingNode - 即将放置的节点
       * @returns 如果可以放置返回true，否则返回false
       */
      validateNode: async (droppingNode: Node) => {
        // 获取鼠标位置
        const { mousePosition } = this.draggingListener;
        
        // 销毁监听器，必须先获取鼠标位置后销毁
        this.destroyListener();

        // 获取节点面板的矩形区域
        const nodePanelRect = nodeContainer.getBoundingClientRect();
        
        // 验证节点是否有效放置在画布中
        const flag = workflowValidator.checkDroppingNode(droppingNode, mousePosition, nodePanelRect);
        
        const proxy = new CustomX6NodeProxy(droppingNode);
        const _data = proxy.getData();
        
        if (!flag) {
          return false;
        }
        
        // 非异步任务节点的校验
        if (_data.getType() !== NodeTypeEnum.ASYNC_TASK) {
          _data
            .validate()
            // 校验节点有误时，加警告
            .catch(() => workflowValidator.addWarning(droppingNode, clickNodeWarningCallback));
          return true;
        }
        
        // 异步任务节点的参数加载
        const data = _data as AsyncTask;
        const isOwnerRef = data.ownerRef === NodeGroupEnum.LOCAL;
        
        // 根据节点来源获取版本列表
        const res = await (isOwnerRef ? getLocalVersionList : getOfficialVersionList)(data.getRef(), data.ownerRef);
        data.version = res.versions.length > 0 ? res.versions[0] : '';
        
        if (isOwnerRef) {
          // 加载本地节点参数
          const {
            inputParameters: inputs,
            outputParameters: outputs,
            description: versionDescription,
          } = await getLocalNodeParams(data.getRef(), data.ownerRef, data.version);
          pushParams(data, inputs, outputs, versionDescription);
        } else {
          // 加载官方/社区节点参数
          const {
            inputParams: inputs,
            outputParams: outputs,
            description: versionDescription,
          } = await getOfficialNodeParams(data.getRef(), data.ownerRef, data.version);
          pushParams(data, inputs, outputs, versionDescription);
        }
        
        // 更新节点数据
        // fix: #I5DXPM
        proxy.setData(data);
        
        // 对节点进行参数校验
        data
          .validate()
          // 校验节点有误时，加警告
          .catch(() => workflowValidator.addWarning(droppingNode, clickNodeWarningCallback));
        return true;
      },
    });
  }

  /**
   * 开始拖拽
   * 
   * 当用户在节点面板中点击并拖动节点时调用此方法。
   * 会创建一个可视化节点并开始拖拽操作。
   * 
   * @param data - 工作流节点数据（IWorkflowNode类型）
   * @param event - 鼠标事件对象
   * 
   * @example
   * // 用户在节点面板中点击了一个Shell节点
   * workflowDnd.drag(shellNodeData, mouseEvent);
   */
  drag(data: IWorkflowNode, event: MouseEvent) {
    // 构建监听器，追踪鼠标位置
    this.buildListener(event);

    // 创建X6节点
    const node = this.graph.createNode({
      shape: 'vue-shape',
      width,
      height,
      component: 'custom-vue-shape',    // 使用Vue组件渲染节点
      ports: { ...PORTS },               // 添加连接桩配置
    });
    
    // 为节点设置数据
    const proxy = new CustomX6NodeProxy(node);
    proxy.setData(data);

    // 开始拖拽
    this.dnd.start(node, event);
  }

  /**
   * 构建鼠标移动监听器
   * 
   * 监听整个文档的鼠标移动事件，用于追踪拖拽过程中的鼠标位置。
   * 这个位置信息用于判断节点是否可以放置。
   * 
   * @param x - 鼠标事件开始的X坐标
   * @param y - 鼠标事件开始的Y坐标
   * @private
   */
  private buildListener({ x, y }: MouseEvent) {
    this.draggingListener.mousePosition = { x, y };
    // 监听整个文档的mousemove事件
    this.draggingListener.listener = listen(document.body, 'mousemove', (e: MouseEvent) => {
      this.draggingListener.mousePosition.x = e.x;
      this.draggingListener.mousePosition.y = e.y;
    });
  }

  /**
   * 销毁监听器
   * 
   * 清理鼠标移动监听器，重置鼠标位置。
   * 在节点放置验证完成后调用。
   * 
   * @private
   */
  private destroyListener() {
    if (this.draggingListener.listener) {
      this.draggingListener.listener.destroy();
    }

    // 重置鼠标位置
    this.draggingListener.mousePosition = { x: -1, y: -1 };
    delete this.draggingListener.listener;
  }
}
