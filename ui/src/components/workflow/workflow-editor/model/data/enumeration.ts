/**
 * 工作流枚举类型定义
 * 
 * 该文件定义了工作流编辑器中使用的所有枚举类型，包括：
 * - 节点引用类型：预定义节点的唯一标识
 * - 参数类型：节点输入输出参数的数据类型
 * - 节点类型：不同类型节点的具体分类
 * - 缩放类型：画布缩放操作的类型
 * - 失败处理模式：任务执行失败时的处理策略
 * - 节点分组：节点的来源和分类
 * - 唯一标识类型：表达式中引用的不同实体类型
 */

/**
 * 节点引用枚举
 * 
 * 定义系统中预定义节点的唯一引用标识符。
 * 这些引用用于在DSL中标识特定类型的节点。
 * 
 * @enum {string}
 * 
 * @example
 * // 在DSL中使用
 * trigger:
 *   type: cron  // 使用CRON引用
 */
export enum NodeRefEnum {
  /** Cron定时触发器引用 */
  CRON = 'cron',
  /** Webhook钩子触发器引用 */
  WEBHOOK = 'webhook',
  /** Shell脚本任务引用 */
  SHELL = 'shell',
}

/**
 * 参数类型枚举
 * 
 * 定义节点输入输出参数的数据类型。
 * 参数类型决定了参数的验证规则和使用方式。
 * 
 * @enum {string}
 * 
 * @example
 * // STRING类型参数
 * inputs:
 *   - name: '用户名'
 *     type: 'STRING'
 *     value: 'admin'
 * 
 * // NUMBER类型参数
 * inputs:
 *   - name: '超时时间'
 *     type: 'NUMBER'
 *     value: '30'
 */
export enum ParamTypeEnum {
  /** 密钥类型 - 用于敏感信息，会在界面上显示为密码样式 */
  SECRET = 'SECRET',
  /** 字符串类型 - 普通的文本内容 */
  STRING = 'STRING',
  /** 数字类型 - 数值参数 */
  NUMBER = 'NUMBER',
  /** 布尔类型 - true/false值 */
  BOOL = 'BOOL',
}

/**
 * 节点类型枚举
 * 
 * 定义工作流中不同类型节点的标识。
 * 节点类型决定了节点的行为、配置选项和执行方式。
 * 
 * @enum {string}
 * 
 * 工作流节点分为两类：
 * 1. 触发器节点：工作流的入口点，触发工作流的执行
 * 2. 任务节点：执行具体业务逻辑的处理单元
 */
export enum NodeTypeEnum {
  /** Cron定时触发器 - 基于cron表达式定时触发工作流 */
  CRON = 'cron',
  /** Webhook触发器 - 通过HTTP请求触发工作流 */
  WEBHOOK = 'webhook',
  // START = 'start',   // 开始节点（预留）
  // END = 'end',       // 结束节点（预留）
  // CONDITION = 'condition',  // 条件节点（预留）
  /** Shell脚本任务 - 执行Shell命令或脚本 */
  SHELL = 'shell',
  /** 异步任务 - 执行外部系统的异步任务 */
  ASYNC_TASK = 'async-task',
}

/**
 * 缩放类型枚举
 * 
 * 定义画布缩放操作的类型。
 * 用于控制工作流画布的显示比例。
 * 
 * @enum {string}
 * 
 * @example
 * // 放大画布
 * zoom(ZoomTypeEnum.IN);
 * 
 * // 适应画布
 * zoom(ZoomTypeEnum.FIT);
 */
export enum ZoomTypeEnum {
  /** 放大 - 以固定步长增加缩放比例 */
  IN = 'IN',
  /** 缩小 - 以固定步长减少缩放比例 */
  OUT = 'OUT',
  /** 原始大小 - 恢复到100%缩放比例 */
  ORIGINAL = 'ORIGINAL',
  /** 适应画布 - 自动调整缩放使内容完整显示 */
  FIT = 'FIT',
  /** 居中显示 - 自动调整缩放并居中（最大100%） */
  CENTER = 'CENTER',
}

/**
 * 失败处理模式枚举
 * 
 * 定义任务执行失败时的处理策略。
 * 当节点执行出错时，可以选择不同的处理方式。
 * 
 * @enum {string}
 * 
 * @example
 * // 忽略失败，继续执行下游任务
 * failure_mode: ignore
 * 
 * // 暂停工作流，等待人工干预
 * failure_mode: suspend
 */
export enum FailureModeEnum {
  /** 忽略 - 继续执行下游任务，不影响整体流程 */
  IGNORE = 'ignore',
  /** 暂停 - 暂停工作流执行，等待人工处理或重试 */
  SUSPEND = 'suspend',
}

/**
 * 节点分组枚举
 * 
 * 定义节点的来源和分类。
 * 节点可以来自不同的分组，用于区分内置节点和自定义节点。
 * 
 * @enum {string}
 * 
 * 节点分组用途：
 * - 前端UI中分类展示节点
 * - 后端API中区分节点来源
 * - 确定节点的加载方式和参数获取接口
 */
export enum NodeGroupEnum {
  /** 触发器分组 - 包含所有触发器类型节点 */
  TRIGGER = 'trigger',
  /** 内置分组 - 包含内置的任务节点（如Shell） */
  INNER = 'inner',
  /** 本地分组 - 用户或组织自定义的节点 */
  LOCAL = 'local',
  /** 官方分组 - 平台官方提供的节点 */
  OFFICIAL = 'official',
  /** 社区分组 - 社区贡献者提供的节点 */
  COMMUNITY = 'community',
}

/**
 * 唯一标识类型枚举
 * 
 * 定义表达式中可以引用的不同实体类型。
 * 用于在表达式编辑器中显示可用的引用选项。
 * 
 * @enum {string}
 * 
 * @example
 * // 引用触发器参数
 * ${触发器参数.trigger_param}
 * 
 * // 引用节点输出
 * ${节点.node_0.output_param}
 * 
 * // 引用缓存
 * ${缓存.cache_name}
 */
export enum RefTypeEnum {
  /** 触发器参数 - 触发器传入的外部参数 */
  TRIGGER_PARAM = '触发器参数',
  /** 全局参数 - 工作流的全局配置参数 */
  GLOBAL_PARAM = '全局参数',
  /** 节点 - 上游任务节点的输出参数 */
  NODE = '节点',
  /** Shell环境变量 - Shell任务中设置的环境变量 */
  SHELL_ENV = 'Shell节点环境变量',
  /** Shell输出参数 - Shell任务的输出结果 */
  SHELL_OUTPUT = 'Shell节点输出参数',
  /** 缓存 - 任务缓存的键值对 */
  CACHE = '缓存',
  /** 目录 - 工作目录或文件路径 */
  DIR = '目录',
}
