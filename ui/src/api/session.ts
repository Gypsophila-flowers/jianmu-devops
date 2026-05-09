import {
  IAuthorizationUrlGettingDto,
  IAuthorizationUrlVo, IOauth2LoggingDto,
  ISessionCreatingDto,
  ISessionVo,
  IThirdPartyTypeVo,
} from '@/api/dto/session';
import { restProxy } from '@/api/index';

/**
 * 用户认证基础路径
 */
export const baseUrl = '/auth';

/**
 * 创建会话（登录）
 * @param dto - 登录参数，包含用户名和密码
 */
export function create(dto: ISessionCreatingDto): Promise<ISessionVo> {
  return restProxy({
    url: `${baseUrl}/login`,
    method: 'post',
    payload: dto,
  });
}

/**
 * 用户注册
 * @param dto - 注册参数，包含用户名、密码、邮箱和昵称
 */
export function register(dto: {
  username: string;
  password: string;
  email: string;
  nickname?: string;
}): Promise<any> {
  return restProxy({
    url: `${baseUrl}/register`,
    method: 'post',
    payload: dto,
  });
}

/**
 * 修改密码
 * @param dto - 包含旧密码和新密码
 */
export function changePassword(dto: {
  oldPassword: string;
  newPassword: string;
}): Promise<void> {
  return restProxy({
    url: `${baseUrl}/change-password`,
    method: 'post',
    payload: dto,
  });
}

/**
 * 获取oauth三方登录方式
 */
export function fetchThirdPartyType() {
  return restProxy<IThirdPartyTypeVo>({
    url: `${baseUrl}/oauth2/third_party_type`,
    method: 'get',
  });
}

/**
 * oauth获取授权url
 */
export function fetchAuthUrl(dto: IAuthorizationUrlGettingDto) {
  return restProxy<IAuthorizationUrlVo>({
    url: `${baseUrl}/oauth2/url`,
    method: 'get',
    payload: dto,
  });
}

/**
 * oauth三方登录
 */
export function authLogin(dto: IOauth2LoggingDto) {
  return restProxy<ISessionVo>({
    url: `${baseUrl}/oauth2/login`,
    method: 'post',
    payload: dto,
  });
}