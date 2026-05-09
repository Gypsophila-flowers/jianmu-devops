/**
 * 工作流工具模块
 * 
 * 该模块提供工作流图形编辑器的各种工具功能，包括：
 * - 画布缩放控制（放大、缩小、适应画布、原始大小）
 * - 选中框样式优化
 * - DSL（Domain Specific Language）生成
 * - 图形数据瘦身处理
 * 
 * 主要功能：
 * 1. 缩放控制：支持多种缩放模式，限制缩放范围在20%-500%之间
 * 2. DSL生成：将工作流图形转换为YAML格式的DSL配置
 * 3. 数据优化：移除不必要的属性，减小保存的数据量
 */

import { Cell, Graph } from '@antv/x6';
import yaml from 'yaml';
import { ZoomTypeEnum } from './data/enumeration';
import { NODE } from '../shape/gengral-config';
import { IGlobal, IWorkflow } from './data/common';
import { CustomX6NodeProxy } from './data/custom-x6-node-proxy';
import { AsyncTask } from './data/node/async-task';

/** 选中边框宽度 */
const { selectedBorderWidth } = NODE;

/** 最小缩放比例：20% */
const MIN_ZOOM = 20;
/** 最大缩放比例：500% */
const MAX_ZOOM = 500;
/** 缩放间隔：每次缩放以10%为单位 */
const ZOOM_INTERVAL = 10;

/**
 * 构建全局配置对象
 * 
 * 处理缓存配置的不同格式，根据缓存数量返回不同的数据结构：
 * - 无缓存：返回undefined
 * - 单个缓存：返回字符串
 * - 多个缓存：返回字符串数组
 * 
 * @param global - 工作流全局配置对象
 * @returns 处理后的全局配置，包含并发数和缓存配置
 * 
 * @example
 * // 无缓存
 * buildGlobal({ concurrent: true, caches: [] })
 * // 返回: { concurrent: true, cache: undefined }
 * 
 * // 单个缓存
 * buildGlobal({ concurrent: 2, caches: ['cache1'] })
 * // 返回: { concurrent: 2, cache: 'cache1' }
 * 
 * // 多个缓存
 * buildGlobal({ concurrent: false, caches: ['cache1', 'cache2'] })
 * // 返回: { concurrent: false, cache: ['cache1', 'cache2'] }
 */
function buildGlobal(global: IGlobal): {
  concurrent: boolean | number;
  cache: string[] | string | undefined;
} {
  // 无缓存配置
  if (!global.caches || global.caches.length === 0) {
    return {
      concurrent: global.concurrent,
      cache: undefined,
    };
  }
  
  // 只有一个缓存，直接返回字符串
  if (global.caches.length === 1) {
    return {
      concurrent: global.concurrent,
      cache: global.caches[0],
    };
  }
  
  // 多个缓存，返回数组
  return {
    concurrent: global.concurrent,
    cache: global.caches,
  };
}

/**
 * WorkflowTool 类 - 工作流图形工具类
 * 
 * 提供工作流编辑器中常用的工具方法，包括：
 * - 缩放控制：放大、缩小、适应画布、恢复原始大小
 * - DSL生成：将图形转换为可执行的YAML配置
 * - 样式优化：调整选中框的显示样式
 * - 数据瘦身：移除冗余属性，减小数据体积
 */
export class WorkflowTool {
  /**
   * X6图形实例
   */
  private readonly graph: Graph;

  /**
   * 构造函数
   * 
   * @param graph - X6图形实例，用于执行各种图形操作
   */
  constructor(graph: Graph) {
    this.graph = graph;
  }

  /**
   * 缩放画布
   * 
   * 支持多种缩放模式：
   * - IN：放大，以10%为单位递增
   * - OUT：缩小，以10%为单位递减
   * - CENTER：居中显示，自动调整到最合适的比例（不超过100%）
   * - FIT：适应画布，使内容完全显示在视口内
   * - ORIGINAL：恢复原始大小（100%）
   * 
   * @param type - 缩放类型枚举值
   * 
   * @example
   * // 放大画布
   * workflowTool.zoom(ZoomTypeEnum.IN);
   * 
   * // 缩小画布
   * workflowTool.zoom(ZoomTypeEnum.OUT);
   * 
   * // 适应画布
   * workflowTool.zoom(ZoomTypeEnum.FIT);
   */
  zoom(type: ZoomTypeEnum) {
    // 四舍五入保证与页面上的显示一致
    let factor = Math.round(this.graph.zoom() * 100);
    const remainder = factor % ZOOM_INTERVAL;
    factor -= remainder;

    switch (type) {
      case ZoomTypeEnum.IN:
        // 放大：增加缩放比例
        factor += ZOOM_INTERVAL;
        factor = factor > MAX_ZOOM ? MAX_ZOOM : factor;
        break;
      case ZoomTypeEnum.OUT:
        // 缩小：减少缩放比例
        factor -= ZOOM_INTERVAL;
        factor = factor < MIN_ZOOM ? MIN_ZOOM : factor;
        if (remainder > 0) {
          factor += ZOOM_INTERVAL;
        }
        break;
      case ZoomTypeEnum.CENTER:
        // 缩放画布内容，使画布内容充满视口
        this.graph.zoomToFit();

        // 四舍五入保证与页面上的显示一致
        factor = Math.round(this.graph.zoom() * 100);
        if (factor > 100) {
          // 大于100%场景
          factor = 100;
        } else if (factor < MIN_ZOOM) {
          // 小于最小场景
          factor = MIN_ZOOM;
        } else {
          this.optimizeSelectionBoxStyle();
          return;
        }
        break;
      case ZoomTypeEnum.FIT:
        // 缩放画布内容，使画布内容充满视口
        this.graph.zoomToFit();

        // 四舍五入保证与页面上的显示一致
        factor = Math.round(this.graph.zoom() * 100);
        if (factor < MIN_ZOOM) {
          factor = MIN_ZOOM;
        } else if (factor > MAX_ZOOM) {
          factor = MAX_ZOOM;
        } else {
          this.optimizeSelectionBoxStyle();
          return;
        }
        break;
      case ZoomTypeEnum.ORIGINAL:
        // 将画布内容中心与视口中心对齐
        // this.graph.centerContent();
        factor = 100;
        break;
    }

    // 以内容中心为基准进行缩放
    const { x, y, width, height } = this.graph.getContentBBox();

    this.graph.zoomTo(factor / 100, {
      center: {
        x: width / 2 + x,
        y: height / 2 + y,
      },
    });

    this.optimizeSelectionBoxStyle();
  }

  /**
   * 优化选中框样式
   * 
   * 根据当前的缩放比例调整选中框的边框宽度，
   * 确保在不同缩放级别下选中框的视觉效果一致
   */
  optimizeSelectionBoxStyle(): void {
    const factor = this.graph.zoom();

    // 获取所有选中框元素并调整样式
    Array.from(this.graph.container.querySelectorAll<SVGElement>('.x6-widget-transform')).forEach(el => {
      const t = selectedBorderWidth * factor;
      el.style.borderWidth = `${t}px`;
      el.style.marginLeft = `-${t}px`;
      el.style.marginTop = `-${t}px`;
    });
  }

  /**
   * 精简图形数据
   * 
   * 在保存工作流之前调用，移除不必要的属性以减小数据体积：
   * - 移除所有工具（tools）
   * - 移除连接桩的填充颜色属性
   * 
   * @param cells - 图形单元格数组（节点和边）
   */
  slimGraphData({ cells }: { cells?: Cell.Properties[] }): void {
    if (!cells) {
      return;
    }

    // 遍历所有单元格进行瘦身处理
    cells.forEach(cell => {
      // 移除所有工具
      delete cell.tools;
      if (cell.shape === 'edge') {
        return;
      }
      // 移除连接桩的填充颜色
      cell.ports.items.forEach((item: any) => {
        delete item.attrs.circle.fill;
      });
    });
  }

  /**
   * 转换为DSL格式
   * 
   * 将工作流图形转换为YAML格式的DSL（Domain Specific Language）配置。
   * DSL是工作流的序列化格式，可以被后端解析并执行。
   * 
   * 转换过程：
   * 1. 从根节点开始遍历，按拓扑顺序获取所有节点
   * 2. 分离触发器节点和任务节点
   * 3. 为每个任务节点生成唯一的引用ID（node_0, node_1, ...）
   * 4. 生成pipeline配置对象
   * 5. 替换参数引用中的节点ID为新的引用ID
   * 6. 添加原始数据作为raw-data字段
   * 
   * @param workflowData - 工作流数据对象，包含名称、描述、全局配置等
   * @returns YAML格式的DSL字符串
   * 
   * @example
   * // 假设有一个简单的工作流
   * const dsl = workflowTool.toDsl({
   *   name: 'my-workflow',
   *   description: '测试工作流',
   *   global: { concurrent: 1, caches: ['cache1'] },
   *   data: '{}'
   * });
   * console.log(dsl);
   * // 输出:
   * // name: my-workflow
   * // description: 测试工作流
   * // global:
   * //   concurrent: 1
   * //   cache: cache1
   * // trigger:
   * //   type: cron
   * //   ...
   * // pipeline:
   * //   node_0:
   * //     type: shell
   * //     ...
   * //
   * // raw-data: {...}
   */
  toDsl(workflowData: IWorkflow): string {
    // 用于存储节点ID和转换后的引用ID
    const idArr: string[] = [];
    const nodeProxyArr: CustomX6NodeProxy[] = [];

    // 获取根节点并开始遍历
    let node = this.graph.getRootNodes()[0];

    // eslint-disable-next-line no-constant-condition
    while (true) {
      idArr.push(node.id);
      nodeProxyArr.push(new CustomX6NodeProxy(node));

      // 获取当前节点的出边
      const edges = this.graph.getOutgoingEdges(node);
      if (!edges) {
        break;
      }
      // 移动到下一个节点
      node = edges[0].getTargetNode()!;
    }

    // 处理触发器节点（如果有的话）
    let trigger;
    if (nodeProxyArr[0].isTrigger()) {
      // 如果第一个节点是触发器，将其分离出来
      idArr.splice(0, 1);
      const nodeProxy = nodeProxyArr.splice(0, 1)[0];
      // eslint-disable-next-line prefer-const
      trigger = nodeProxy.getData().toDsl();
    }
    
    // 构建pipeline配置
    const pipeline: {
      // eslint-disable-next-line @typescript-eslint/ban-types
      [key: string]: object;
    } = {};

    // 用于存储ID映射，用于替换参数引用
    const idMap = new Map<string, string>();
    nodeProxyArr.forEach((nodeProxy, index) => {
      // 生成新的引用ID
      const ref = `node_${index}`;
      const nodeData = nodeProxy.getData();

      // 只有在异步任务节点有输出参数时，才有可能被下游节点引用
      if (nodeData instanceof AsyncTask && (nodeData as AsyncTask).outputs.length > 0) {
        idMap.set(idArr[index], ref);
      }

      // 将节点数据转换为DSL格式
      pipeline[ref] = nodeData.toDsl();
    });

    // 生成YAML字符串
    let dsl = yaml.stringify({
      name: workflowData.name,
      description: workflowData.description,
      global: buildGlobal(workflowData.global),
      trigger,
      pipeline,
    });

    // 替换参数引用中的旧ID为新ID
    idMap.forEach(
      (value, key) =>
        // TODO 待完善，优化成正则表达式提取方式
        (dsl = dsl.replaceAll('${' + key + '.', '${' + value + '.')),
    );

    // 添加原始数据
    dsl += '\n\n' + `raw-data: ${JSON.stringify(workflowData.data)}`;

    return dsl;
  }
}
