package dev.jianmu.api.controller;

import dev.jianmu.api.dto.UpdateUserDto;
import dev.jianmu.api.dto.UserVo;
import dev.jianmu.api.jwt.UserContextHolder;
import dev.jianmu.application.exception.DataNotFoundException;
import dev.jianmu.user.aggregate.User;
import dev.jianmu.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理控制器
 *
 * <p>该控制器负责处理用户管理相关的API请求，包括用户信息查询、更新等功能。
 *
 * <p><b>主要功能：</b>
 * <ul>
 *   <li>获取当前登录用户信息</li>
 *   <li>获取所有用户列表</li>
 *   <li>根据ID获取用户信息</li>
 *   <li>更新用户信息</li>
 *   <li>删除用户</li>
 * </ul>
 *
 * @author JianMu Dev
 * @class UserController
 * @description 用户管理控制器
 */
@RestController
@RequestMapping("users")
@Tag(name = "用户管理", description = "用户管理API，提供用户信息查询和管理功能")
public class UserController {

    private final UserRepository userRepository;

    /**
     * 构造函数
     *
     * @param userRepository 用户仓储
     */
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 获取当前登录用户信息
     *
     * <p>通过JWT令牌获取当前登录用户的信息。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：GET</li>
     *   <li>请求路径：/users/me</li>
     *   <li>Authorization: Bearer {token}</li>
     * </ul>
     *
     * <p><b>响应信息：</b>
     * <ul>
     *   <li>200 OK - 返回用户信息</li>
     *   <li>401 Unauthorized - 未登录或令牌无效</li>
     * </ul>
     *
     * @return ResponseEntity<UserVo> 用户信息响应
     */
    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public ResponseEntity<UserVo> getCurrentUser() {
        String userId = UserContextHolder.getUser().getId();
        
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("用户不存在"));
        
        return ResponseEntity.ok(toUserVo(user));
    }

    /**
     * 获取所有用户列表
     *
     * <p>获取系统中所有用户的列表（不包含密码）。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：GET</li>
     *   <li>请求路径：/users</li>
     * </ul>
     *
     * @return ResponseEntity<List<UserVo>> 用户列表响应
     */
    @GetMapping
    @Operation(summary = "获取用户列表", description = "获取系统中所有用户的列表")
    public ResponseEntity<List<UserVo>> getAllUsers() {
        List<UserVo> users = userRepository.findAll().stream()
                .map(this::toUserVo)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(users);
    }

    /**
     * 根据ID获取用户信息
     *
     * <p>根据用户ID获取指定用户的信息。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：GET</li>
     *   <li>请求路径：/users/{id}</li>
     * </ul>
     *
     * @param id 用户ID
     * @return ResponseEntity<UserVo> 用户信息响应
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户详细信息")
    public ResponseEntity<UserVo> getUserById(
            @Parameter(description = "用户ID", required = true)
            @PathVariable String id) {
        
        var user = userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("用户不存在"));
        
        return ResponseEntity.ok(toUserVo(user));
    }

    /**
     * 更新用户信息
     *
     * <p>更新当前登录用户的基本信息（不包括密码）。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：PUT</li>
     *   <li>请求路径：/users/me</li>
     * </ul>
     *
     * @param updateUserDto 更新用户信息请求
     * @return ResponseEntity<UserVo> 更新后的用户信息
     */
    @PutMapping("/me")
    @Operation(summary = "更新用户信息", description = "更新当前登录用户的基本信息")
    public ResponseEntity<UserVo> updateCurrentUser(@Valid @RequestBody UpdateUserDto updateUserDto) {
        String userId = UserContextHolder.getUser().getId();
        
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("用户不存在"));

        // 更新用户信息
        if (updateUserDto.getNickname() != null) {
            user.setNickname(updateUserDto.getNickname());
        }
        if (updateUserDto.getEmail() != null) {
            user.setEmail(updateUserDto.getEmail());
        }
        if (updateUserDto.getAvatarUrl() != null) {
            user.setAvatarUrl(updateUserDto.getAvatarUrl());
        }
        user.setUpdatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        userRepository.update(user);
        
        return ResponseEntity.ok(toUserVo(user));
    }

    /**
     * 删除用户
     *
     * <p>根据用户ID删除指定用户。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：DELETE</li>
     *   <li>请求路径：/users/{id}</li>
     * </ul>
     *
     * @param id 用户ID
     * @return ResponseEntity<Void> 空响应
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "根据用户ID删除指定用户")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable String id) {
        
        if (!userRepository.findById(id).isPresent()) {
            throw new DataNotFoundException("用户不存在");
        }
        
        userRepository.deleteById(id);
        
        return ResponseEntity.noContent().build();
    }

    /**
     * 将User实体转换为UserVo
     *
     * @param user 用户实体
     * @return UserVo 用户信息响应对象
     */
    private UserVo toUserVo(User user) {
        return UserVo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}