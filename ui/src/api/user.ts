import { restProxy } from '@/api/index';

/**
 * 用户管理API基础路径
 */
const baseUrl = '/users';

/**
 * 用户信息接口
 */
export interface IUserVo {
  id: string;
  username: string;
  email: string;
  nickname: string;
  avatarUrl?: string;
  enabled: boolean;
  createdAt?: string;
  updatedAt?: string;
}

/**
 * 获取当前用户信息
 */
export function fetchCurrentUser(): Promise<IUserVo> {
  return restProxy({
    url: `${baseUrl}/me`,
    method: 'get',
  });
}

/**
 * 获取所有用户列表
 */
export function fetchUsers(): Promise<IUserVo[]> {
  return restProxy({
    url: baseUrl,
    method: 'get',
  });
}

/**
 * 根据ID获取用户信息
 */
export function fetchUserById(id: string): Promise<IUserVo> {
  return restProxy({
    url: `${baseUrl}/${id}`,
    method: 'get',
  });
}

/**
 * 更新当前用户信息
 */
export function updateCurrentUser(dto: {
  nickname?: string;
  email?: string;
  avatarUrl?: string;
}): Promise<IUserVo> {
  return restProxy({
    url: `${baseUrl}/me`,
    method: 'put',
    payload: dto,
  });
}

/**
 * 删除用户
 */
export function deleteUser(id: string): Promise<void> {
  return restProxy({
    url: `${baseUrl}/${id}`,
    method: 'delete',
  });
}