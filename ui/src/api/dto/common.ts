/**
 * 通用数据传输对象（DTO）和值对象（VO）定义
 * 
 * 该文件定义了API请求和响应中使用的通用数据结构，包括：
 * - 错误信息结构
 * - 基础值对象
 * - 分页相关结构
 * - 版本信息结构
 */

import { NodeTypeEnum } from '@/api/dto/enumeration';

/**
 * 错误信息响应对象
 * 
 * 当API请求失败时，后端返回的错误详情结构。
 * 
 * @interface IErrorMessageVo
 * 
 * @example
 * // 典型的错误响应
 * {
 *   statusCode: 404,
 *   timestamp: "2024-01-15T10:30:00Z",
 *   message: "项目不存在",
 *   description: "ID为123的项目已被删除"
 * }
 */
export interface IErrorMessageVo extends Readonly<{
  /** HTTP状态码 */
  statusCode: number;
  
  /** 错误发生时间 */
  timestamp: Date;
  
  /** 简要错误信息 */
  message: string;
  
  /** 详细错误描述 */
  description: string;
}> {
}

/**
 * 抽象值对象基类
 * 
 * 包含创建时间和修改信息的基础对象。
 * 大多数业务对象都会继承这个基类。
 * 
 * @interface BaseVo
 */
export interface BaseVo extends Readonly<{
  /**
   * 创建时间
   * @description 资源创建的时间，格式为ISO 8601字符串
   */
  createdTime: string;

  /**
   * 最后修改人
   * @description 最后修改该资源的用户标识
   */
  lastModifiedBy: string;

  /**
   * 最后修改时间
   * @description 资源最后一次修改的时间
   */
  lastModifiedTime: string;
}> {
}

/**
 * 分页查询请求参数
 * 
 * 用于分页查询的请求参数，包含分页信息和筛选条件。
 * 
 * @interface IPageDto
 * 
 * @example
 * // 查询第2页，每页20条
 * {
 *   pageNum: 2,
 *   pageSize: 20,
 *   name: 'workflow'
 * }
 */
export interface IPageDto extends Readonly<{
  /**
   * 页码
   * @description 从1开始的页码
   */
  pageNum: number;

  /**
   * 每页个数
   * @description 每页显示的记录数量
   */
  pageSize: number;

  /**
   * 分页类型
   * @description 可选的筛选条件，节点类型
   */
  type?: NodeTypeEnum;

  /**
   * 节点名
   * @description 可选的筛选条件，节点名称关键词
   */
  name?: string;
}> {
}

/**
 * 分页响应值对象
 * 
 * 标准分页查询的响应结构。
 * 
 * @interface IPageVo
 * @template T - 列表中元素的类型
 * 
 * @example
 * // 响应示例
 * {
 *   total: 100,        // 总共100条记录
 *   pages: 5,          // 共5页
 *   list: [...],        // 当前页的数据
 *   pageNum: 2          // 当前第2页
 * }
 */
export interface IPageVo<T> extends Readonly<{
  /**
   * 总个数
   * @description 符合条件的总记录数
   */
  total: number;

  /**
   * 总页数
   * @description 根据pageSize计算的总页数
   */
  pages: number;

  /**
   * 数据
   * @description 当前页的数据列表
   */
  list: T[];

  /**
   * 当前页码
   * @description 与请求中的pageNum对应
   */
  pageNum: number;
}> {
}

/**
 * 版本信息
 * 
 * 表示资源的版本详情。
 * 
 * @interface IVersionVo
 */
export interface IVersionVo extends Readonly<{
  /** 版本号 */
  versionNo: string;
  
  /** 发行说明URL */
  releaseUrl: string;
}> {
}

/**
 * Hub节点分页响应
 * 
 * 用于官方/社区节点库的分页查询响应。
 * 与标准的IPageVo相比，字段名称不同（使用Java风格命名）。
 * 
 * @interface IHubNodePageVo
 * @template T - 列表中元素的类型
 * 
 * @example
 * // 响应示例
 * {
 *   totalElements: 500,
 *   pageNum: 1,
 *   totalPages: 10,
 *   size: 50,
 *   content: [...]  // 当前页的节点列表
 * }
 */
export interface IHubNodePageVo<T>
  extends Readonly<{
    /**
     * 总个数
     * @description 符合条件的总元素数
     */
    totalElements: number;
    
    /**
     * 当前页
     * @description 当前页码（从1开始）
     */
    pageNum: number;
    
    /**
     * 总页数
     * @description 计算得到的总页数
     */
    totalPages: number;
    
    /**
     * 当前加载数量
     * @description 当前页的实际数据条数
     */
    size: number;
    
    /**
     * 数据
     * @description 当前页的内容数组
     */
    content: T[];
  }> {
}
