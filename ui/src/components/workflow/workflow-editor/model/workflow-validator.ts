/**
 * 工作流校验模块
 * 
 * 该模块负责对工作流图形进行业务逻辑校验，确保工作流的正确性和完整性。
 * 主要校验内容包括：
 * 1. 节点数量检查：工作流必须至少包含一个可执行节点（Shell或AsyncTask）
 * 2. 节点连接检查：确保所有节点都已正确连接（除单个节点的情况外）
 * 3. 单节点工作流检查：允许只有一个可执行节点的情况
 * 4. 触发器检查：确保工作流只有一个触发器节点
 * 5. 各节点的业务校验：根据节点类型进行特定的参数校验
 * 
 * 校验失败时会抛出错误，错误信息会显示具体是哪个节点出现问题
 */

import { Cell, CellView, Graph, JQuery, Node, Point } from '@antv/x6';
import { NodeTypeEnum } from './data/enumeration';
import { CustomX6NodeProxy } from './data/custom-x6-node-proxy';
import nodeWarningIcon from '../svgs/node-warning.svg';
import { IWorkflow } from './data/common';
import { globalT as t } from '@/utils/i18n';

/**
 * 点击节点警告图标的回调函数类型
 * @description 当用户点击节点上的警告图标时调用的回调函数
 */
export type ClickNodeWarningCallbackFnType = (nodeId: string) => void;

/**
 * 判断节点是否已添加警告工具
 * 
 * @param node - X6节点实例
 * @returns 如果节点已有警告工具返回true，否则返回false
 * 
 * 警告工具是在节点右上角显示的警告图标，用于提示用户该节点存在配置问题
 */
function isWarning(node: Node): boolean {
  return node.hasTool('button');
}

/**
 * WorkflowValidator 类 - 工作流图形校验器
 * 
 * 负责对工作流图形进行全面的业务逻辑校验，包括：
 * - 节点存在性检查
 * - 节点连接完整性检查
 * - 触发器唯一性检查
 * - 节点参数校验
 * - 节点警告图标的添加与移除
 * 
 * 校验过程中会收集各类错误，并以友好的方式向用户展示
 */
export class WorkflowValidator {
  /**
   * X6图形实例，用于访问画布中的所有节点和边
   */
  private readonly graph: Graph;
  
  /**
   * Vue组件代理实例，用于调用$warning等全局方法显示警告消息
   */
  private readonly proxy: any;
  
  /**
   * 工作流数据对象，包含工作流的基本信息和DSL数据
   */
  private readonly workflowData: IWorkflow;

  /**
   * 构造函数
   * 
   * @param graph - X6图形实例
   * @param proxy - Vue组件代理实例
   * @param workflowData - 工作流数据对象
   */
  constructor(graph: Graph, proxy: any, workflowData: IWorkflow) {
    this.graph = graph;
    this.proxy = proxy;
    this.workflowData = workflowData;
  }

  /**
   * 为节点添加警告图标
   * 
   * 当节点存在配置错误或警告信息时，在节点右上角显示一个警告图标。
   * 用户点击该图标可以查看具体的警告详情。
   * 
   * @param node - 需要添加警告图标的节点
   * @param clickNodeWarningCallback - 点击警告图标时的回调函数
   * 
   * @example
   * // 为验证失败的节点添加警告
   * validator.addWarning(node, (nodeId) => {
   *   console.log(`用户点击了节点 ${nodeId} 的警告图标`);
   * });
   */
  addWarning(node: Node, clickNodeWarningCallback: ClickNodeWarningCallbackFnType): void {
    // 如果节点已有警告工具，则不重复添加
    if (isWarning(node)) {
      return;
    }

    // 为节点添加工具按钮
    node.addTools({
      name: 'button',
      args: {
        // 定义按钮的SVG标记
        markup: [
          {
            tagName: 'image',
            attrs: {
              width: 24,                      // 图标宽度
              height: 24,                     // 图标高度
              'xlink:href': nodeWarningIcon,  // 警告图标URL
              cursor: 'pointer',               // 鼠标样式为手型
            },
          },
        ],
        x: '100%',                             // 水平位置：节点右侧
        y: 0,                                  // 垂直位置：节点顶部
        offset: { x: -13, y: -11 },           // 偏移量，微调图标位置
        onClick: ({ cell: { id } }: { e: JQuery.MouseDownEvent; cell: Cell; view: CellView }) =>
          clickNodeWarningCallback(id),       // 点击事件处理
      },
    });
  }

  /**
   * 移除节点的警告图标
   * 
   * 当节点的问题被修复后，调用此方法移除警告图标。
   * 如果节点没有警告图标，则不做任何操作。
   * 
   * @param node - 需要移除警告图标的节点
   */
  removeWarning(node: Node): void {
    // 检查节点是否真的有警告工具
    if (!isWarning(node)) {
      return;
    }

    // 移除指定名称的工具
    node.removeTool('button');
  }

  /**
   * 校验所有节点
   * 
   * 这是工作流校验的核心方法，会对整个工作流图形进行全面检查：
   * 1. 检查工作流是否至少有一个节点
   * 2. 检查是否至少包含一个可执行节点（Shell或AsyncTask）
   * 3. 检查所有节点是否都已正确连接
   * 4. 对每个节点进行业务参数校验
   * 
   * @throws 当校验失败时抛出Error异常，错误信息包含具体的失败原因
   * @returns 校验通过时不返回任何值
   * 
   * @example
   * try {
   *   await validator.checkNodes();
   *   console.log('工作流校验通过');
   * } catch (error) {
   *   console.error('校验失败:', error.message);
   * }
   */
  async checkNodes(): Promise<void> {
    // 获取画布中的所有节点
    const nodes = this.graph.getNodes();

    // 校验1：检查工作流是否为空
    if (nodes.length === 0) {
      // 没有节点，抛出错误
      throw new Error(t('workflowValidator.noNodes'));
    }

    // 校验2：检查是否至少有一个可执行节点
    if (
      !nodes.find(node =>
        [NodeTypeEnum.SHELL, NodeTypeEnum.ASYNC_TASK].includes(new CustomX6NodeProxy(node).getData().getType()),
      )
    ) {
      // 工作流中必须至少包含一个Shell节点或异步任务节点
      throw new Error(t('workflowValidator.atLeastOneShellOrTaskNode'));
    }

    // 校验3：检查所有节点是否都已连接
    if (nodes.length > 1) {
      // 收集所有已连接的节点
      const nodeSet = new Set<Node>();
      this.graph.getEdges().forEach(edge => {
        // 获取每条边的源节点和目标节点
        nodeSet.add(edge.getSourceNode()!);
        nodeSet.add(edge.getTargetNode()!);
      });
      
      // 转换为数组
      const connectedNodes = Array.from(nodeSet);
      
      // 找出未连接的节点
      const unconnectedNodes = nodes.filter(node => !connectedNodes.includes(node));
      
      // 如果存在未连接的节点，抛出错误
      if (unconnectedNodes.length > 0) {
        const nodeName = new CustomX6NodeProxy(unconnectedNodes[0]).getData().getName();
        throw new Error(`${nodeName}${t('workflowValidator.nodeNotConnected')}`);
      }
    }

    // 校验4：对每个节点进行业务参数校验
    // 将节点代理转换为工作流节点数据对象
    const workflowNodes = nodes.map(node => new CustomX6NodeProxy(node).getData(this.graph, this.workflowData));
    
    // 遍历每个节点进行校验
    for (const workflowNode of workflowNodes) {
      try {
        // 调用节点的validate方法进行业务校验
        await workflowNode.validate();
      } catch ({ errors }) {
        // 校验失败，抛出包含节点名称的错误信息
        throw new Error(`${workflowNode.getName()}${t('workflowValidator.node')}${errors[0].message}`);
      }
    }
  }

  /**
   * 检查拖放的节点是否可以放置
   * 
   * 在用户拖拽节点到画布时调用，用于判断节点是否可以放置在当前位置。
   * 主要检查：
   * 1. 节点是否放置在节点面板区域（不允许）
   * 2. 是否已经存在触发器节点（不允许有多个触发器）
   * 
   * @param node - 被拖拽的节点
   * @param mousePosition - 鼠标当前位置
   * @param nodePanelRect - 节点面板的矩形区域
   * @returns 如果可以放置返回true，否则返回false
   */
  checkDroppingNode(node: Node, mousePosition: Point.PointLike, nodePanelRect: DOMRect): boolean {
    // 检查放置位置是否有效
    if (!this.checkDroppingPosition(mousePosition, nodePanelRect)) {
      return false;
    }

    // 检查触发器数量限制
    if (!this.checkTrigger(node)) {
      return false;
    }

    return true;
  }

  /**
   * 检查节点放置位置是否有效
   * 
   * @param mousePosition - 鼠标位置
   * @param nodePanelRect - 节点面板区域
   * @returns 如果位置有效（在面板外）返回true，否则返回false
   * 
   * 逻辑：检查鼠标坐标是否在节点面板区域内，如果在则不允许放置
   */
  private checkDroppingPosition(mousePosition: Point.PointLike, nodePanelRect: DOMRect): boolean {
    const { x: mousePosX, y: mousePosY } = mousePosition;
    const { x, y, width, height } = nodePanelRect;
    const maxX = x + width;
    const maxY = y + height;

    // 判断鼠标是否在节点面板矩形范围内
    if (mousePosX >= x && mousePosX <= maxX && mousePosY >= y && mousePosY <= maxY) {
      // 在节点面板中拖放时，不允许放置
      return false;
    }

    return true;
  }

  /**
   * 检查触发器节点数量限制
   * 
   * 工作流中只能有一个触发器节点（Cron或Webhook）。
   * 如果当前拖放的节点是触发器，且画布中已存在触发器，则不允许放置。
   * 
   * @param droppingNode - 被拖放的节点
   * @returns 如果可以放置返回true，否则返回false
   */
  private checkTrigger(droppingNode: Node): boolean {
    // 检查是否为触发器节点
    if (!new CustomX6NodeProxy(droppingNode).isTrigger()) {
      // 非触发器节点，不受限制
      return true;
    }

    // 表示当前拖放的节点为触发器
    // 检查画布中是否已存在触发器节点
    const currentTrigger = this.graph.getNodes().find(node => new CustomX6NodeProxy(node).isTrigger());

    if (currentTrigger) {
      // 画布中已有触发器，显示警告并拒绝放置
      this.proxy.$warning(t('workflowValidator.onlyOneTrigger'));
      return false;
    }

    return true;
  }
}
