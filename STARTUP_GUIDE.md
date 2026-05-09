# 建木 JianMu DevOps 平台 - 启动运行指南

<div align="center">

![JianMu Logo](https://jianmu-blog.assets.dghub.cn/jianmu-blog/1.80.1/assets/blog-source/%E7%AC%AC%E4%B8%80%E5%B1%8F%E5%9B%BE.png)

**面向DevOps领域的极易扩展的开源无代码/低代码(GitOps)工具**

[![License](https://img.shields.io/badge/liscense-MulanPSL--2.0-green.svg)](LICENSE)
[![JDK](https://img.shields.io/badge/JDK-11+-lightgrey.svg)]()
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-lightgrey.svg)]()
[![Vue](https://img.shields.io/badge/Vue-3-lightgrey.svg)]()

**GitHub仓库**: https://github.com/Gypsophila-flowers/jianmu-devops

</div>

---

## 📋 目录

1. [环境要求](#环境要求)
2. [快速启动（使用H2内存数据库）](#快速启动使用h2内存数据库)
3. [生产环境启动（使用MySQL）](#生产环境启动使用mysql)
4. [前端启动](#前端启动)
5. [配置说明](#配置说明)
6. [常见问题](#常见问题)
7. [开发调试技巧](#开发调试技巧)
8. [API文档](#api文档)

---

## 🖥️ 环境要求

### 必需环境

| 环境 | 版本要求 | 说明 |
|------|---------|------|
| **JDK** | 11 或更高 | 推荐使用JDK 17或JDK 21以获得更好的性能 |
| **Maven** | 3.6+ | 用于构建Java后端项目 |
| **Node.js** | 14+ | 推荐使用Node.js 18 LTS |
| **npm** | 6+ | 用于管理前端依赖 |
| **MySQL** | 8.0+ | 仅生产环境需要，H2可用于开发 |

### 可选环境

| 环境 | 版本要求 | 说明 |
|------|---------|------|
| **Docker** | 20.10+ | 用于容器化部署 |
| **Redis** | 6.0+ | 用于分布式锁和事件发布（可选） |
| **Git** | 2.30+ | 用于代码管理和Webhook功能 |

### 安装检查

```bash
# 检查Java版本
java -version
# 输出应该显示 11.0 或更高版本

# 检查Maven版本
mvn -version
# 输出应该显示 Apache Maven 3.6+

# 检查Node.js版本
node -v
# 输出应该显示 v14+ (推荐 v18 LTS)

# 检查npm版本
npm -v
# 输出应该显示 6+
```

---

## 🚀 快速启动（使用H2内存数据库）

这种方式不需要安装MySQL，适合开发调试。使用H2内存数据库，所有数据在重启后会丢失。

### 步骤1：克隆代码

```bash
# 如果还没有克隆过
git clone https://github.com/Gypsophila-flowers/jianmu-devops.git

# 进入项目目录
cd jianmu-devops
```

### 步骤2：编译后端项目

```bash
# 编译项目（跳过测试以加快速度）
mvn clean package -DskipTests
```

**编译成功标志：**
```
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  5-10 分钟（首次编译）
[INFO] ------------------------------------------------------------------------
```

### 步骤3：启动后端服务

```bash
# 进入API模块目录
cd api

# 启动后端服务（使用dev配置文件，H2内存数据库）
java -jar target/jianmu-api-*.jar --spring.profiles.active=dev
```

**启动成功标志：**
```
2026-05-09 12:00:00.000  INFO 12345 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8081 (http) with context path ''
2026-05-09 12:00:00.000  INFO 12345 --- [           main] d.jianmu.api.JianmuApplication          : Started JianmuApplication in 10.5 seconds
```

### 步骤4：访问服务

| 服务 | 地址 | 说明 |
|------|------|------|
| **API服务** | http://localhost:8081 | 后端API服务 |
| **Swagger文档** | http://localhost:8081/swagger-ui.html | API接口文档 |
| **H2控制台** | http://localhost:8081/h2-console | 数据库管理（可选） |

### 步骤5：安装前端依赖并启动

```bash
# 新开一个终端窗口
cd jianmu-devops/ui

# 安装依赖（首次运行需要）
npm install
```

> **提示**：首次安装可能需要5-10分钟，取决于网络速度。如果npm install太慢，可以尝试使用淘宝镜像：
> ```bash
> npm install --registry=https://registry.npmmirror.com
> ```

### 步骤6：启动前端开发服务器

```bash
# 启动Vue开发服务器
npm run serve
```

**启动成功标志：**
```
  App running at:
  - Local:   http://localhost:8080/
  - Network: http://192.168.x.x:8080/
```

### 步骤7：访问前端页面

打开浏览器访问：http://localhost:8080

默认管理员账号：
- **用户名**: admin
- **密码**: 123456

---

## 🗄️ 生产环境启动（使用MySQL）

### 步骤1：安装并配置MySQL

#### 使用Docker安装MySQL（推荐）

```bash
# 启动MySQL容器
docker run -d \
  --name jianmu-mysql \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=jianmu123456 \
  -e MYSQL_DATABASE=jianmu \
  -e MYSQL_USER=jianmu \
  -e MYSQL_PASSWORD=jianmu123456 \
  mysql:8.0 \
  --character-set-server=utf8mb4 \
  --collation-server=utf8mb4_unicode_ci
```

#### 或手动安装MySQL

1. 下载并安装 MySQL 8.0+: https://dev.mysql.com/downloads/mysql/
2. 创建数据库和用户：

```sql
-- 登录MySQL
mysql -u root -p

-- 创建数据库
CREATE DATABASE jianmu CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户（可选）
CREATE USER 'jianmu'@'%' IDENTIFIED BY 'jianmu123456';
GRANT ALL PRIVILEGES ON jianmu.* TO 'jianmu'@'%';
FLUSH PRIVILEGES;
```

### 步骤2：创建配置文件

在 `api/src/main/resources/` 目录下创建 `application-dev.yml` 文件：

```yaml
# application-dev.yml - 开发环境配置文件

spring:
  datasource:
    # MySQL数据源配置
    url: jdbc:mysql://localhost:3306/jianmu?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: jianmu
    password: jianmu123456
    driver-class-name: com.mysql.cj.jdbc.Driver

  # Flyway数据库迁移配置
  flyway:
    enabled: true
    clean-disabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    baseline-version: 1
    out-of-order: false
    validate-on-migrate: true

# JPA/Hibernate配置
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false

# JWT配置
jianmu:
  api:
    # 管理员密码（生产环境请使用强密码）
    adminPasswd: 123456

# 存储配置
storage:
  filepath: ./data

# 注册中心配置
registry:
  url: https://jianmuhub.com
```

### 步骤3：编译并打包

```bash
# 在项目根目录执行
mvn clean package -DskipTests

# 或者使用Maven Wrapper（如果项目包含）
./mvnw clean package -DskipTests
```

### 步骤4：启动服务

```bash
# 使用MySQL配置启动
java -jar api/target/jianmu-api-*.jar --spring.profiles.active=dev
```

### 步骤5：验证服务

```bash
# 测试API是否正常响应
curl http://localhost:8081/api/v1/health

# 应该返回 {"status":"UP"} 或类似的健康状态
```

---

## 🎨 前端启动

### 开发模式启动

```bash
# 进入前端目录
cd ui

# 安装依赖（仅首次需要）
npm install

# 启动开发服务器
npm run serve

# 服务启动后访问 http://localhost:8080
```

### 前端开发服务器特性

- **热模块替换 (HMR)**: 代码修改后自动更新页面
- **Source Maps**: 便于调试
- **代理配置**: 自动代理API请求到后端服务器

### 生产环境构建

```bash
# 构建生产版本
npm run build

# 预览构建结果
npm run preview
```

构建产物在 `ui/dist` 目录下，可以部署到任何静态文件服务器。

---

## ⚙️ 配置说明

### 后端主要配置项

#### 数据库配置

| 配置项 | 说明 | 示例值 |
|--------|------|--------|
| `spring.datasource.url` | 数据库连接URL | `jdbc:mysql://localhost:3306/jianmu` |
| `spring.datasource.username` | 数据库用户名 | `jianmu` |
| `spring.datasource.password` | 数据库密码 | `jianmu123456` |

#### JWT认证配置

| 配置项 | 说明 | 示例值 |
|--------|------|--------|
| `jianmu.api.jwtSecret` | JWT加密密钥 | `你的64位密钥` |
| `jianmu.api.jwtExpirationMs` | Token过期时间(毫秒) | `86400000` (24小时) |
| `jianmu.api.adminPasswd` | 管理员初始密码 | `123456` |

**生成JWT密钥：**
```bash
# 使用OpenSSL生成随机密钥
openssl rand -base64 64
```

#### Worker配置

| 配置项 | 说明 | 示例值 |
|--------|------|--------|
| `jianmu.worker.secret` | Worker连接密钥 | `worker-secret-key` |

#### OAuth2配置（可选）

如果需要启用第三方登录（GitHub、GitLab等），需要配置OAuth2应用信息：

```yaml
oauth2:
  # GitHub配置示例
  github:
    client-id: your-github-client-id
    client-secret: your-github-client-secret
```

### 前端配置

前端配置在 `ui/.env.development` 和 `ui/.env.production` 文件中：

```bash
# .env.development - 开发环境
VITE_API_BASE_URL=http://localhost:8081
VITE_APP_TITLE=JianMu DevOps

# .env.production - 生产环境
VITE_API_BASE_URL=https://your-api-domain.com
VITE_APP_TITLE=JianMu DevOps
```

---

## ❓ 常见问题

### 1. 端口冲突

如果8080或8081端口被占用：

**后端端口修改：**
```yaml
# application-dev.yml
server:
  port: 8082  # 改为其他端口
```

**前端端口修改：**
```bash
# 在ui目录下创建 vite.config.js
export default {
  server: {
    port: 3000  # 改为其他端口
  }
}
```

### 2. Maven依赖下载失败

如果Maven依赖下载失败，尝试配置国内镜像：

```xml
<!-- ~/.m2/settings.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                              http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <mirrors>
    <mirror>
      <id>aliyun-maven</id>
      <name>Aliyun Maven</name>
      <url>https://maven.aliyun.com/repository/public</url>
      <mirrorOf>central</mirrorOf>
    </mirror>
  </mirrors>

</settings>
```

### 3. 前端依赖安装失败

```bash
# 清除npm缓存
npm cache clean --force

# 使用淘宝镜像
npm config set registry https://registry.npmmirror.com

# 重新安装
rm -rf node_modules package-lock.json
npm install
```

### 4. 数据库迁移失败

如果Flyway数据库迁移失败：

```bash
# 清理并重新迁移（开发环境）
mvn flyway:clean flyway:migrate -DskipTests

# 或者删除数据库重新创建
DROP DATABASE IF EXISTS jianmu;
CREATE DATABASE jianmu CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 5. CORS跨域问题

如果前端无法访问后端API，检查CORS配置：

```yaml
# application-dev.yml
server:
  cors:
    allowed-origins: http://localhost:8080
```

---

## 🛠️ 开发调试技巧

### 1. 使用IDE调试

#### IntelliJ IDEA

1. 导入项目为Maven项目
2. 设置JDK为11+
3. 运行 `JianmuApplication` 主类
4. 设置断点进行调试

#### VS Code

安装扩展：
- Extension Pack for Java
- Spring Boot Extension Pack

### 2. 前端调试

```bash
# 启动带调试的前端服务
npm run serve -- --mode development

# 浏览器打开开发者工具
# Vue DevTools 可在Chrome商店安装
```

### 3. 查看日志

```bash
# 实时查看后端日志
tail -f api/logs/application.log

# 或在启动时指定日志文件
java -jar api/target/jianmu-api-*.jar --logging.file.path=./logs
```

### 4. 数据库调试

使用H2控制台（开发模式）:
- 地址: http://localhost:8081/h2-console
- JDBC URL: `jdbc:h2:mem:db`
- 用户名: `sa`
- 密码: `sa`

使用MySQL客户端:
```bash
mysql -u jianmu -p -h localhost jianmu
```

### 5. API测试

使用Swagger UI测试API:
- 地址: http://localhost:8081/swagger-ui.html

使用curl测试:
```bash
# 登录获取Token
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# 使用Token访问受保护的API
curl -X GET http://localhost:8081/api/v1/projects \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

## 📚 API文档

### 认证接口

| 接口 | 方法 | 说明 | 示例 |
|------|------|------|------|
| `/auth/login` | POST | 用户登录 | [查看](#用户登录) |
| `/auth/register` | POST | 用户注册 | [查看](#用户注册) |
| `/auth/change-password` | POST | 修改密码 | [查看](#修改密码) |
| `/auth/oauth2/login` | POST | OAuth2登录 | [查看](#oauth2登录) |

### 用户管理接口

| 接口 | 方法 | 说明 | 示例 |
|------|------|------|------|
| `/users/me` | GET | 获取当前用户 | [查看](#获取当前用户) |
| `/users/me` | PUT | 更新当前用户 | [查看](#更新当前用户) |
| `/users` | GET | 获取用户列表 | [查看](#获取用户列表) |
| `/users/{id}` | DELETE | 删除用户 | [查看](#删除用户) |

### API使用示例

#### 用户登录

```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123456"
  }'

# 响应示例
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "id": "user-123",
  "username": "admin",
  "email": "admin@example.com",
  "nickname": "管理员",
  "type": "Bearer"
}
```

#### 用户注册

```bash
curl -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "Password123!",
    "email": "newuser@example.com",
    "nickname": "新用户"
  }'

# 响应示例
{
  "id": "user-456",
  "username": "newuser",
  "email": "newuser@example.com",
  "nickname": "新用户",
  "enabled": true,
  "createdAt": "2026-05-09T12:00:00"
}
```

#### 修改密码

```bash
curl -X POST http://localhost:8081/auth/change-password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "oldPassword": "OldPassword123!",
    "newPassword": "NewPassword123!"
  }'
```

#### 获取当前用户

```bash
curl -X GET http://localhost:8081/users/me \
  -H "Authorization: Bearer YOUR_TOKEN"
```

#### 获取用户列表

```bash
curl -X GET http://localhost:8081/users \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 🔧 故障排查清单

如果服务启动失败，按以下顺序检查：

1. **环境检查**
   - [ ] Java版本是否为11+
   - [ ] Maven是否安装并可用
   - [ ] Node.js和npm是否安装

2. **端口检查**
   - [ ] 8080端口是否被占用（前端）
   - [ ] 8081端口是否被占用（后端）
   - [ ] 3306端口是否被占用（MySQL）

3. **数据库检查**
   - [ ] MySQL是否正在运行
   - [ ] 数据库是否创建
   - [ ] 用户权限是否正确

4. **配置检查**
   - [ ] application-dev.yml是否创建
   - [ ] 数据库连接信息是否正确
   - [ ] 所有必需的配置项是否填写

5. **依赖检查**
   - [ ] Maven依赖是否下载完成
   - [ ] npm依赖是否安装完成

---

## 📞 获取帮助

如果你在使用过程中遇到问题：

1. **查看项目Issues**: https://github.com/Gypsophila-flowers/jianmu-devops/issues
2. **查看官方文档**: https://docs.jianmu.dev
3. **联系开发者**: 在GitHub仓库提交Issue

---

## 📄 许可证

本项目基于 [MulanPSL-2.0](LICENSE) 开源许可证。

---

<div align="center">

**祝你开发愉快！ 🚀**

</div>
