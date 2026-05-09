/**
 * 工作流节点加载模块
 * 
 * 该模块负责从不同来源加载工作流中可用的节点类型：
 * - 内置触发器：Cron定时触发、Webhook钩子触发
 * - 内置任务：Shell脚本执行
 * - 本地节点库：用户自定义的节点
 * - 官方节点库：平台提供的官方节点
 * - 社区节点库：社区贡献的节点
 * 
 * 主要功能：
 * 1. 加载不同类型的节点列表
 * 2. 支持分页加载和关键词搜索
 * 3. 将节点数据转换为工作流图形节点
 */

import { IWorkflowNode } from './data/common';
import { Cron } from './data/node/cron';
import { Webhook } from './data/node/webhook';
import { Shell } from './data/node/shell';
import { AsyncTask } from './data/node/async-task';
import { fetchNodeLibraryList, getOfficialNodes } from '@/api/view-no-auth';
import { NodeTypeEnum } from '@/api/dto/enumeration';
import { INodeParameterVo } from '@/api/dto/node-definitions';
import { ParamTypeEnum } from '@/components/workflow/workflow-editor/model/data/enumeration';

/**
 * 分页信息接口
 * @interface IPageInfo
 * @description 描述分页查询的结果
 */
interface IPageInfo {
  /** 当前页码（从1开始） */
  pageNum: number;
  /** 总页数 */
  totalPages: number;
  /** 当前页的数据内容 */
  content: IWorkflowNode[];
}

/**
 * pushParams 函数 - 向工作流节点添加输入输出参数
 * 
 * 将节点定义中的输入参数和输出参数添加到工作流节点数据中。
 * 这些参数用于节点间的数据传递和流程控制。
 * 
 * @param data - 工作流节点数据对象（AsyncTask实例）
 * @param inputs - 节点定义的输入参数列表
 * @param outputs - 节点定义的输出参数列表
 * @param versionDescription - 参数版本描述信息
 * 
 * @example
 * // 添加输入参数
 * pushParams(nodeData, [
 *   { ref: 'input1', name: '输入1', type: 'STRING', value: '', required: true },
 *   { ref: 'input2', name: '输入2', type: 'NUMBER', value: '100', required: false }
 * ], [], 'v1.0.0');
 */
export const pushParams = (
  data: AsyncTask,
  inputs: INodeParameterVo[],
  outputs: INodeParameterVo[],
  versionDescription: string,
) => {
  // 设置参数版本描述
  data.versionDescription = versionDescription;
  
  // 处理输入参数：将参数定义转换为节点参数对象
  if (inputs) {
    inputs.forEach(item => {
      data.inputs.push({
        ref: item.ref,                    // 参数引用名（用于DSL中的${ref}语法）
        name: item.name,                  // 参数显示名称
        type: item.type as ParamTypeEnum, // 参数类型枚举
        value: (item.value || '').toString(), // 参数默认值
        required: item.required,          // 是否为必填参数
        description: item.description,    // 参数描述信息
      });
    });
  }
  
  // 处理输出参数：同上
  if (outputs) {
    outputs.forEach(item => {
      data.outputs.push({
        ref: item.ref,
        name: item.name,
        type: item.type as ParamTypeEnum,
        value: (item.value || '').toString(),
        required: item.required,
        description: item.description,
      });
    });
  }
};

/**
 * WorkflowNode 类 - 工作流节点管理器
 * 
 * 负责从不同来源加载工作流可用的节点类型，包括：
 * - 内置触发器节点（Cron、Webhook）
 * - 内置任务节点（Shell）
 * - 自定义节点库（本地节点、官方节点、社区节点）
 * 
 * 支持分页加载和关键词搜索功能
 */
export class WorkflowNode {
  // 构造函数
  // eslint-disable-next-line @typescript-eslint/no-empty-function
  constructor() {}

  /**
   * 加载内置触发器节点
   * 
   * 触发器是工作流的入口点，用于触发工作流的执行。
   * 目前支持两种内置触发器：
   * - Cron：基于cron表达式的定时触发
   * - Webhook：基于HTTP请求的 webhook 触发
   * 
   * @param keyword - 可选的关键词过滤条件
   * @returns 符合条件的工作流节点数组
   * 
   * @example
   * // 获取所有内置触发器
   * const triggers = workflowNode.loadInnerTriggers();
   * 
   * // 根据关键词过滤
   * const cronTrigger = workflowNode.loadInnerTriggers('cron');
   */
  loadInnerTriggers(keyword?: string): IWorkflowNode[] {
    // 创建内置触发器实例数组
    const arr: IWorkflowNode[] = [new Cron(), new Webhook()];

    // 如果有关键词，则进行大小写不敏感的过滤
    return keyword ? arr.filter(item => item.getName().toLowerCase().includes(keyword.toLowerCase())) : arr;
  }

  /**
   * 加载内置任务节点
   * 
   * 任务节点是工作流的核心执行单元，用于执行业务逻辑。
   * 目前支持一种内置任务：
   * - Shell：执行Shell脚本或命令
   * 
   * @param keyword - 可选的关键词过滤条件
   * @returns 符合条件的工作流节点数组
   */
  loadInnerNodes(keyword?: string): IWorkflowNode[] {
    // 创建内置任务实例数组
    const arr: IWorkflowNode[] = [new Shell()];

    // 关键词过滤
    return keyword ? arr.filter(item => item.getName().toLowerCase().includes(keyword.toLowerCase())) : arr;
  }

  /**
   * 加载本地节点库
   * 
   * 本地节点是用户或组织自定义的节点，可以用于特定业务场景。
   * 使用分页加载以支持大量节点的场景。
   * 
   * @param pageNum - 当前页码（从1开始）
   * @param pageSize - 每页显示的节点数量
   * @param keyword - 可选的关键词过滤条件
   * @returns 分页信息，包含当前页的节点列表
   * 
   * @example
   * // 加载第一页的本地节点，每页10个
   * const result = await workflowNode.loadLocalNodes(1, 10);
   * console.log(result.content); // 当前页的节点数组
   * console.log(result.totalPages); // 总页数
   */
  async loadLocalNodes(pageNum: number, pageSize: number, keyword?: string): Promise<IPageInfo> {
    // 调用API获取本地节点列表
    const {
      list,                       // 节点列表
      pageNum: currentPageNum,    // 当前页码
      pages: totalPages,          // 总页数
    } = await fetchNodeLibraryList({
      pageNum,
      pageSize,
      type: NodeTypeEnum.LOCAL,   // 指定为本地节点类型
      name: keyword,
    });
    
    // 将API返回的节点数据转换为工作流节点对象
    const arr: IWorkflowNode[] = list.map(item => new AsyncTask(item.ownerRef, item.ref, item.name, item.icon));
    
    // 返回分页信息和节点数组
    return {
      pageNum: currentPageNum,
      totalPages,
      content: arr,
    };
  }

  /**
   * 加载官方节点库
   * 
   * 官方节点是由平台提供的标准节点，具有稳定的API和完整的文档。
   * 
   * @param pageNum - 当前页码
   * @param pageSize - 每页数量
   * @param keyword - 关键词过滤
   * @returns 分页信息和节点数组
   */
  async loadOfficialNodes(pageNum: number, pageSize: number, keyword?: string): Promise<IPageInfo> {
    // 调用API获取官方节点列表
    const {
      content,                    // 节点内容数组
      pageNum: currentPageNum,   // 当前页码
      totalPages,                 // 总页数
    } = await getOfficialNodes({
      pageNum,
      pageSize,
      name: keyword,
    });
    
    // 转换为工作流节点对象
    const arr: IWorkflowNode[] = content.map(item => new AsyncTask(item.ownerRef, item.ref, item.name, item.icon));
    
    return {
      pageNum: currentPageNum,
      totalPages,
      content: arr,
    };
  }

  /**
   * 加载社区节点库
   * 
   * 社区节点是由社区贡献者开发的节点，功能可能不如官方节点稳定，
   * 但提供了更多的扩展性。系统会根据用户需求不断添加新的社区节点。
   * 
   * @param pageNum - 当前页码
   * @param pageSize - 每页数量
   * @param keyword - 关键词过滤
   * @returns 分页信息和节点数组
   */
  async loadCommunityNodes(pageNum: number, pageSize: number, keyword?: string): Promise<IPageInfo> {
    // 调用API获取社区节点列表
    const {
      content,
      pageNum: currentPageNum,
      totalPages,
    } = await getOfficialNodes({
      pageNum,
      pageSize,
      name: keyword,
      type: NodeTypeEnum.COMMUNITY,  // 指定为社区节点类型
    });
    
    // 转换为工作流节点对象
    const arr: IWorkflowNode[] = content.map(item => new AsyncTask(item.ownerRef, item.ref, item.name, item.icon));
    
    return {
      pageNum: currentPageNum,
      totalPages,
      content: arr,
    };
  }
}
