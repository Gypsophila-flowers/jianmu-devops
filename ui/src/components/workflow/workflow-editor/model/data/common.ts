/**
 * 工作流数据结构定义
 * 
 * 该文件定义了工作流编辑器中使用的核心数据结构和接口，包括：
 * - 节点数据接口：描述节点的通用属性和行为
 * - 全局配置接口：描述工作流的全局设置
 * - 工作流数据接口：描述完整的工作流配置
 * - 校验函数类型：用于参数和缓存的校验
 * 
 * 这些接口和类型是工作流图形编辑器和DSL转换的基础
 */

import { RuleItem } from 'async-validator';
import { NodeTypeEnum } from './enumeration';
import { ISelectableParam } from '../../../workflow-expression-editor/model/data';

/**
 * 校验触发方式类型
 * @description 指定校验是在失去焦点时触发还是值变化时触发
 */
type TriggerValue = 'blur' | 'change';

/**
 * 自定义校验规则项
 * @description 扩展async-validator的RuleItem，添加trigger属性
 */
export interface CustomRuleItem extends RuleItem {
  /** 校验触发方式 */
  trigger?: TriggerValue;
}

/**
 * 自定义校验规则类型
 * @description 可以是单个规则或规则数组
 */
export type CustomRule = CustomRuleItem | CustomRuleItem[];

/**
 * IWorkflowNode 接口 - 工作流节点数据接口
 * 
 * 定义所有工作流节点必须实现的方法和属性。
 * 工作流节点包括：
 * - 触发器节点：Cron、Webhook
 * - 任务节点：Shell、AsyncTask
 * 
 * 每个节点类型都需要实现以下接口方法
 */
export interface IWorkflowNode {
  /**
   * 获取节点引用名
   * @returns 节点的唯一引用标识符
   * 
   * @example
   * // Shell节点的引用名是 'shell'
   * // Cron节点的引用名是 'cron'
   */
  getRef(): string;

  /**
   * 获取节点显示名称
   * @returns 节点的友好显示名称
   * 
   * @example
   * // 返回 'Shell脚本' 或 '定时触发'
   */
  getName(): string;

  /**
   * 获取节点类型
   * @returns 节点类型枚举值
   * 
   * @example
   * // 返回 NodeTypeEnum.SHELL 或 NodeTypeEnum.CRON
   */
  getType(): NodeTypeEnum;

  /**
   * 获取节点图标URL
   * @returns 图标的URL或base64编码
   */
  getIcon(): string;

  /**
   * 获取节点文档URL
   * @returns 节点的使用文档链接
   */
  getDocUrl(): string;

  /**
   * 构建可选择的参数
   * 
   * 用于表达式编辑器中，显示当前节点可用的输入参数。
   * 上游节点的输出参数可以作为下游节点的输入。
   * 
   * @param nodeId - 当前节点ID
   * @returns 可选择的参数配置对象
   */
  buildSelectableParam(nodeId: string): ISelectableParam | undefined;

  /**
   * 获取表单校验规则
   * 
   * 返回节点配置表单的校验规则。
   * 规则由async-validator库定义。
   * 
   * @returns 表单字段到校验规则的映射
   * 
   * @example
   * // 返回格式
   * {
   *   script: [{ required: true, message: '请输入脚本内容' }],
   *   timeout: [{ type: 'number', min: 1, message: '超时时间必须大于0' }]
   * }
   */
  getFormRules(): Record<string, CustomRule>;

  /**
   * 校验节点配置
   * 
   * 验证节点的必填参数是否已填写，参数值是否合法。
   * 校验失败时抛出包含错误信息的异常。
   * 
   * @throws Error - 校验失败时抛出错误，包含具体的错误信息
   * @returns 校验通过时不返回任何值
   */
  validate(): Promise<void>;

  /**
   * 转换为DSL格式
   * 
   * 将节点配置转换为YAML DSL中的对应格式。
   * DSL是工作流的序列化格式，用于存储和传输。
   * 
   * @returns DSL格式的对象
   */
  // eslint-disable-next-line @typescript-eslint/ban-types
  toDsl(): object;
}

/**
 * IGlobal 接口 - 工作流全局配置
 * 
 * 定义工作流的全局配置选项，包括：
 * - 并发执行设置
 * - 缓存配置列表
 */
export interface IGlobal {
  /** 
   * 并发执行设置
   * - number: 最大并发任务数
   * - boolean: true表示不限制并发，false表示串行执行
   */
  concurrent: number | boolean;
  
  /** 缓存配置列表，每个缓存由名称标识 */
  caches: string[];
}

/**
 * IWorkflow 接口 - 工作流完整数据
 * 
 * 描述一个完整的工作流，包含基本信息、全局配置和图形数据
 */
export interface IWorkflow {
  /** 工作流名称，必须唯一 */
  name: string;
  
  /** 工作流描述，说明工作流的用途 */
  description?: string;
  
  /** 所属项目/项目组的ID */
  groupId: string;
  
  /** 工作流的全局配置 */
  global: IGlobal;
  
  /** 
   * 图形DSL数据
   * 包含画布中所有节点和边的JSON序列化数据
   */
  data: string;
}

/**
 * 参数值校验函数类型
 * @description 用于动态校验参数值的函数
 */
export type ValidateParamFn = (value: string) => void;

/**
 * 缓存名称校验函数类型
 * @description 用于校验缓存名称是否有效（已存在等）
 */
export type ValidateCacheFn = (name: string) => void;
