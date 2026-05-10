/**
 * 权限管理 API - 前端调用接口
 */

// ===============================================
// 角色管理 API
// ===============================================

/**
 * 获取所有角色列表
 */
export function fetchRoles(): Promise<IRoleVo[]> {
  return restProxy({
    url: '/roles',
    method: 'get',
  });
}

/**
 * 获取角色详情
 */
export function fetchRoleById(id: string): Promise<IRoleVo> {
  return restProxy({
    url: `/roles/${id}`,
    method: 'get',
  });
}

/**
 * 创建角色
 */
export function createRole(dto: IRoleDto): Promise<IRoleVo> {
  return restProxy({
    url: '/roles',
    method: 'post',
    payload: dto,
  });
}

/**
 * 更新角色
 */
export function updateRole(id: string, dto: IRoleDto): Promise<IRoleVo> {
  return restProxy({
    url: `/roles/${id}`,
    method: 'put',
    payload: dto,
  });
}

/**
 * 删除角色
 */
export function deleteRole(id: string): Promise<void> {
  return restProxy({
    url: `/roles/${id}`,
    method: 'delete',
  });
}

/**
 * 获取角色权限
 */
export function fetchRolePermissions(roleId: string): Promise<IPermissionVo[]> {
  return restProxy({
    url: `/roles/${roleId}/permissions`,
    method: 'get',
  });
}

/**
 * 分配角色权限
 */
export function assignRolePermissions(roleId: string, permissionIds: string[]): Promise<void> {
  return restProxy({
    url: `/roles/${roleId}/permissions`,
    method: 'put',
    payload: permissionIds,
  });
}

// ===============================================
// 权限管理 API
// ===============================================

/**
 * 获取权限列表（树形结构）
 */
export function fetchPermissions(): Promise<IPermissionVo[]> {
  return restProxy({
    url: '/permissions',
    method: 'get',
  });
}

/**
 * 获取扁平化权限列表
 */
export function fetchFlatPermissions(): Promise<IPermissionVo[]> {
  return restProxy({
    url: '/permissions/flat',
    method: 'get',
  });
}

// ===============================================
// 用户角色管理 API
// ===============================================

/**
 * 获取用户角色
 */
export function fetchUserRoles(userId: string): Promise<IRoleVo[]> {
  return restProxy({
    url: `/users/${userId}/roles`,
    method: 'get',
  });
}

/**
 * 分配用户角色
 */
export function assignUserRoles(userId: string, roleIds: string[]): Promise<void> {
  return restProxy({
    url: `/users/${userId}/roles`,
    method: 'put',
    payload: { userId, roleIds },
  });
}

/**
 * 获取用户权限
 */
export function fetchUserPermissions(userId: string): Promise<IPermissionVo[]> {
  return restProxy({
    url: `/users/${userId}/permissions`,
    method: 'get',
  });
}

// ===============================================
// 资源权限管理 API
// ===============================================

/**
 * 获取资源权限列表
 */
export function fetchResourcePermissions(resourceType: string, resourceId: string): Promise<IResourcePermissionVo[]> {
  return restProxy({
    url: `/resources/${resourceType}/${resourceId}/permissions`,
    method: 'get',
  });
}

/**
 * 授予资源权限
 */
export function grantResourcePermission(dto: IResourcePermissionDto): Promise<void> {
  return restProxy({
    url: '/resources/permissions',
    method: 'post',
    payload: dto,
  });
}

/**
 * 撤销资源权限
 */
export function revokeResourcePermission(params: {
  resourceType: string;
  resourceId: string;
  grantType: string;
  granteeId: string;
}): Promise<void> {
  return restProxy({
    url: '/resources/permissions',
    method: 'delete',
    payload: params,
  });
}

/**
 * 检查资源权限
 */
export function checkResourcePermission(params: {
  userId: string;
  resourceType: string;
  resourceId: string;
  permission: string;
}): Promise<boolean> {
  return restProxy({
    url: '/resources/check-permission',
    method: 'get',
    payload: params,
  });
}

// ===============================================
// 类型定义
// ===============================================

/**
 * 角色信息
 */
export interface IRoleVo {
  id: string;
  name: string;
  code: string;
  description?: string;
  type: 'SYSTEM' | 'CUSTOM';
  status: boolean;
  createdAt?: string;
  updatedAt?: string;
  createdBy?: string;
  permissions?: IPermissionVo[];
}

/**
 * 角色创建/更新请求
 */
export interface IRoleDto {
  id?: string;
  name: string;
  code: string;
  description?: string;
  permissionIds?: string[];
  status?: boolean;
}

/**
 * 权限信息
 */
export interface IPermissionVo {
  id: string;
  name: string;
  code: string;
  type: 'MENU' | 'BUTTON' | 'API' | 'DATA';
  resourceType?: string;
  description?: string;
  parentId?: string;
  sortOrder?: number;
  status?: boolean;
  createdAt?: string;
  updatedAt?: string;
  children?: IPermissionVo[];
  checked?: boolean;
}

/**
 * 资源权限信息
 */
export interface IResourcePermissionVo {
  id: string;
  resourceType: string;
  resourceId: string;
  grantType: 'USER' | 'ROLE';
  granteeId: string;
  granteeName?: string;
  permissions: string[];
  createdAt?: string;
  updatedAt?: string;
  createdBy?: string;
}

/**
 * 资源权限请求
 */
export interface IResourcePermissionDto {
  resourceType: string;
  resourceId: string;
  grantType: 'USER' | 'ROLE';
  granteeId: string;
  permissions: string[];
}

/**
 * 分配角色请求
 */
export interface IAssignRoleDto {
  userId: string;
  roleIds: string[];
}
