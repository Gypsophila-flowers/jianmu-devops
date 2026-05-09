/**
 * API枚举类型定义
 * 
 * 该文件定义了建木系统中使用的枚举类型，用于API请求和响应的数据结构。
 * 枚举类型覆盖了工作流执行、项目管理、节点定义等核心业务场景。
 */

/**
 * 流程执行记录状态枚举
 * 
 * 描述工作流实例的执行状态。
 * 
 * @enum {string}
 * 
 * 状态流转：
 * INIT -> RUNNING -> FINISHED
 *                |-> TERMINATED (手动终止)
 *                |-> SUSPENDED (暂停等待)
 */
export enum WorkflowExecutionRecordStatusEnum {
  /** 初始化 - 工作流实例已创建但尚未开始执行 */
  INIT = 'INIT',
  /** 运行中 - 工作流正在执行中 */
  RUNNING = 'RUNNING',
  /** 已完成 - 工作流正常执行完毕 */
  FINISHED = 'FINISHED',
  /** 已终止 - 工作流被手动终止 */
  TERMINATED = 'TERMINATED',
  /** 已暂停 - 工作流暂停等待人工干预 */
  SUSPENDED = 'SUSPENDED',
}

/**
 * 项目状态枚举
 * 
 * 描述项目级别的状态，主要用于项目导入和同步。
 * 
 * @enum {string}
 */
export enum ProjectStatusEnum {
  /** 初始化 - 项目刚创建或导入 */
  INIT = 'INIT',
  /** 运行中 - 项目正在同步或处理中 */
  RUNNING = 'RUNNING',
  /** 失败 - 项目处理失败 */
  FAILED = 'FAILED',
  /** 成功 - 项目处理成功 */
  SUCCEEDED = 'SUCCEEDED',
  /** 暂停 - 项目暂停 */
  SUSPENDED = 'SUSPENDED',
}

/**
 * 任务状态枚举
 * 
 * 描述单个任务节点的状态。
 * 比工作流执行状态更细化，包含更多的中间状态。
 * 
 * @enum {string}
 * 
 * 典型状态流转：
 * INIT -> WAITING -> RUNNING -> SUCCEEDED
 *                      |-> FAILED -> (可重试) -> RUNNING
 *                      |-> SKIPPED (被跳过)
 *                      |-> SUSPENDED (被暂停)
 *                      |-> IGNORED (被忽略)
 */
export enum TaskStatusEnum {
  /** 初始化 - 任务刚创建 */
  INIT = 'INIT',
  /** 等待中 - 等待前置任务完成 */
  WAITING = 'WAITING',
  /** 运行中 - 任务正在执行 */
  RUNNING = 'RUNNING',
  /** 已跳过 - 任务被条件跳过 */
  SKIPPED = 'SKIPPED',
  /** 失败 - 任务执行失败 */
  FAILED = 'FAILED',
  /** 成功 - 任务执行成功 */
  SUCCEEDED = 'SUCCEEDED',
  /** 已暂停 - 任务被暂停等待 */
  SUSPENDED = 'SUSPENDED',
  /** 已忽略 - 任务被配置为忽略失败继续执行 */
  IGNORED = 'IGNORED',
}

/**
 * 项目导入类型枚举
 * 
 * 定义从外部导入项目的协议类型。
 * 
 * @enum {string}
 */
export enum ProjectImporterTypeEnum {
  /** SSH协议 - 使用SSH密钥认证 */
  SSH = 'SSH',
  /** HTTPS协议 - 使用用户名密码认证 */
  HTTPS = 'HTTPS',
}

/**
 * DSL来源枚举
 * 
 * 定义工作流DSL配置的来源。
 * 
 * @enum {string}
 */
export enum DslSourceEnum {
  /** Git仓库 - DSL存储在Git仓库中 */
  GIT = 'GIT',
  /** 本地 - DSL直接存储在本地 */
  LOCAL = 'LOCAL',
}

/**
 * DSL类型枚举
 * 
 * 定义DSL配置的类型。
 * 
 * @enum {string}
 */
export enum DslTypeEnum {
  /** 工作流 - 完整的流程编排 */
  WORKFLOW = 'WORKFLOW',
  /** 流水线 - 简化的流水线配置 */
  PIPELINE = 'PIPELINE',
}

/**
 * 任务参数类型枚举
 * 
 * 定义任务输入输出参数的方向。
 * 
 * @enum {string}
 */
export enum TaskParamTypeEnum {
  /** 输入参数 - 从上游节点或触发器传入 */
  INPUT = 'INPUT',
  /** 输出参数 - 向下游节点传递 */
  OUTPUT = 'OUTPUT',
}

/**
 * 触发类型枚举
 * 
 * 定义工作流的触发方式。
 * 
 * @enum {string}
 */
export enum TriggerTypeEnum {
  /** Webhook触发 - 通过HTTP请求触发 */
  WEBHOOK = 'WEBHOOK',
  /** Cron触发 - 定时触发 */
  CRON = 'CRON',
  /** 手动触发 - 人工手动启动 */
  MANUAL = 'MANUAL',
}

/**
 * 节点类型枚举
 * 
 * 定义节点的运行环境类型。
 * 
 * @enum {string}
 */
export enum NodeTypeEnum {
  /** Docker容器 - 在Docker容器中执行 */
  DOCKER = 'DOCKER',
  /** Shell执行器 - 直接执行Shell命令 */
  SHELL = 'SHELL',
  /** 本地 - 本地执行环境 */
  LOCAL = 'LOCAL',
  /** 社区 - 社区提供的运行环境 */
  COMMUNITY = 'COMMUNITY',
}

/**
 * 归属类型枚举
 * 
 * 定义节点或项目的所有者类型。
 * 
 * @enum {string}
 */
export enum OwnerTypeEnum {
  /** 个人 - 归属于个人用户 */
  PERSONAL = 'PERSONAL',
  /** 组织 - 归属于组织/团队 */
  ORGANIZATION = 'ORGANIZATION',
  /** 本地 - 本地节点库 */
  LOCAL = 'LOCAL',
}

/**
 * 密钥管理器类型枚举
 * 
 * 定义密钥存储的管理方式。
 * 
 * @enum {string}
 */
export enum CredentialManagerTypeEnum {
  /** 本地存储 - 使用本地数据库存储 */
  LOCAL = 'local',
  /** Vault存储 - 使用HashiCorp Vault存储 */
  VAULT = 'vault',
}

/**
 * Webhook请求状态枚举
 * 
 * 描述Webhook请求的处理结果状态。
 * 
 * @enum {string}
 */
export enum WebhookRequstStateEnum {
  /** 成功 - 请求被成功接收 */
  OK = 'OK',
  /** 不可接受 - 请求格式或参数不正确 */
  NOT_ACCEPTABLE = 'NOT_ACCEPTABLE',
  /** 未授权 - 认证失败 */
  UNAUTHORIZED = 'UNAUTHORIZED',
  /** 未找到 - 目标资源不存在 */
  NOT_FOUND = 'NOT_FOUND',
  /** 未知错误 - 其他未知错误 */
  UNKNOWN = 'UNKNOWN',
}

/**
 * 参数类型枚举
 * 
 * 定义任务参数的数据类型。
 * 
 * @enum {string}
 */
export enum ParamTypeEnum {
  /** 密钥类型 - 敏感信息加密存储 */
  SECRET = 'SECRET',
}

/**
 * 项目排序枚举
 * 
 * 定义项目列表的排序方式。
 * 
 * @enum {string}
 */
export enum SortTypeEnum {
  /** 默认排序 - 按名称或创建时间排序 */
  DEFAULT_SORT = 'DEFAULT_SORT',
  /** 按最后修改时间 - 最近修改的排在前面 */
  LAST_MODIFIED_TIME = 'LAST_MODIFIED_TIME',
  /** 按最后执行时间 - 最近执行的排在前面 */
  LAST_EXECUTION_TIME = 'LAST_EXECUTION_TIME',
}

/**
 * 失败处理模式枚举
 * 
 * 定义任务执行失败时的处理策略。
 * 
 * @enum {string}
 */
export enum FailureModeEnum {
  /** 忽略 - 忽略失败继续执行 */
  IGNORE = 'IGNORE',
  /** 暂停 - 暂停工作流等待处理 */
  SUSPEND = 'SUSPEND',
}

/**
 * 节点定义可见类型枚举
 * 
 * 定义节点对其他用户或组织的可见性。
 * 
 * @enum {string}
 */
export enum VisibleTypeEnum {
  /** 公开 - 所有用户可见 */
  PUBLIC = 'PUBLIC',
  /** 私有 - 仅所有者可见 */
  PRIVATE = 'PRIVATE',
  /** 组织可见 - 组织内成员可见 */
  ORGANIZATION_VISIBLE = 'ORGANIZATION_VISIBLE',
}

/**
 * Worker类型枚举
 * 
 * 定义执行节点的Worker类型。
 * Worker是实际执行任务的代理服务。
 * 
 * @enum {string}
 */
export enum WorkerTypeEnum {
  /** 内嵌Worker - 与主服务集成的小型执行器 */
  EMBEDDED = 'EMBEDDED',
  /** Docker Worker - 在Docker容器中执行任务 */
  DOCKER = 'DOCKER',
  /** Kubernetes Worker - 在K8s集群中执行任务 */
  KUBERNETES = 'KUBERNETES',
  /** Shell Worker - 通过SSH执行Shell命令 */
  SHELL = 'SHELL',
}

/**
 * Worker状态枚举
 * 
 * 定义Worker节点的在线状态。
 * 
 * @enum {string}
 */
export enum WorkerStatusEnum {
  /** 在线 - Worker正常运行中 */
  ONLINE = 'ONLINE',
  /** 离线 - Worker不可用 */
  OFFLINE = 'OFFLINE',
}
