package dev.jianmu.api.controller;

import dev.jianmu.api.vo.ErrorMessage;
import dev.jianmu.application.exception.DataNotFoundException;
import dev.jianmu.infrastructure.exception.DBException;
import jakarta.validation.ConstraintViolationException;
import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.vault.VaultException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.HandlerMethod;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REST全局异常处理器
 *
 * <p>该类负责统一处理REST API抛出的各类异常，将异常信息转换为规范的错误响应。
 * 使用@RestControllerAdvice注解，实现全局异常拦截和处理。
 *
 * <p><b>处理的异常类型：</b>
 * <ul>
 *   <li>BindException - 请求参数绑定验证失败</li>
 *   <li>SQLException - 数据库操作异常</li>
 *   <li>DuplicateKeyException - 主键冲突/数据重复</li>
 *   <li>VaultException - HashiCorp Vault密钥库异常</li>
 *   <li>DataNotFoundException - 数据未找到异常</li>
 *   <li>RuntimeException - 运行时异常</li>
 *   <li>Exception - 其他未分类异常</li>
 * </ul>
 *
 * <p><b>响应格式：</b>
 * <pre>{@code
 * {
 *   "statusCode": 400,
 *   "timestamp": "2024-01-01T10:00:00",
 *   "message": "错误信息描述",
 *   "description": "请求描述"
 * }
 * }</pre>
 *
 * @author Ethan Liu
 * @class RestExceptionHandler
 * @description REST全局异常处理器，统一处理API异常并返回规范错误响应
 * @create 2021-04-06 20:40
 */
@RestControllerAdvice
public class RestExceptionHandler {
    /**
     * 日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    /**
     * 处理请求参数绑定验证异常
     *
     * <p>当请求参数不满足验证规则时触发，如缺少必填字段、格式不正确等。
     *
     * @param ex 绑定异常对象
     * @param request Web请求对象
     * @return ErrorMessage 错误信息响应
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage validationBodyException(BindException ex, WebRequest request) {
        // 获取验证失败的字段错误列表
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        // 返回包含首个错误信息的响应
        return ErrorMessage.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .message(fieldErrors.get(0).getDefaultMessage())
                .description(request.getDescription(false))
                .build();
    }

    /**
     * 处理SQL执行异常
     *
     * <p>当SQL语句执行出错时触发，如语法错误、表不存在等。
     *
     * @param ex SQL异常对象
     * @param request Web请求对象
     * @return ErrorMessage 错误信息响应
     */
    @ExceptionHandler({BadSqlGrammarException.class, SQLException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage sqlException(Exception ex, WebRequest request) {
        // 记录SQL异常日志
        logger.error("Sql异常: ", ex);
        return ErrorMessage.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .message("Sql执行错误")
                .description(request.getDescription(false))
                .build();
    }

    /**
     * 处理数据完整性约束异常
     *
     * <p>当违反数据库完整性约束时触发，如外键引用失败、唯一索引冲突等。
     *
     * @param ex SQL完整性约束异常
     * @param request Web请求对象
     * @return ErrorMessage 错误信息响应
     */
    @ExceptionHandler({SQLIntegrityConstraintViolationException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage sqlIntegrityConstraintViolationException(Exception ex, WebRequest request) {
        return ErrorMessage.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .message("数据完整性错误")
                .description(request.getDescription(false))
                .build();
    }

    /**
     * 处理主键冲突异常
     *
     * <p>当插入或更新数据时主键重复时触发。
     *
     * @param ex 主键冲突异常
     * @param request Web请求对象
     * @return ErrorMessage 错误信息响应
     */
    @ExceptionHandler({DuplicateKeyException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage duplicateKeyException(DuplicateKeyException ex, WebRequest request) {
        // 根据异常原因返回具体错误信息
        String message = ex.getCause().getMessage().contains("workflow_name_UNIQUE")
                ? "项目名称不能重复"
                : "主键重复";
        return ErrorMessage.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .message(message)
                .description(request.getDescription(false))
                .build();
    }

    /**
     * 处理Vault密钥库异常
     *
     * <p>当与HashiCorp Vault密钥库通信出错时触发。
     *
     * @param ex Vault异常对象
     * @param request Web请求对象
     * @return ErrorMessage 错误信息响应
     */
    @ExceptionHandler(VaultException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage vaultException(VaultException ex, WebRequest request) {
        // 记录警告日志
        logger.warn("Vault异常: {}", ex.getMessage());
        return ErrorMessage.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .description(request.getDescription(false))
                .build();
    }

    /**
     * 处理数据未找到异常
     *
     * <p>当查询的数据不存在时触发。
     *
     * @param ex 数据未找到异常
     * @param request Web请求对象
     * @return ErrorMessage 错误信息响应
     */
    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage dataNotFoundException(DataNotFoundException ex, WebRequest request) {
        // 记录错误日志
        logger.error("数据异常: ", ex);
        return ErrorMessage.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .description(request.getDescription(false))
                .build();
    }

    /**
     * 处理数据库数据未找到异常
     *
     * <p>当数据库查询结果为空时触发。
     *
     * @param ex 数据库异常对象
     * @param request Web请求对象
     * @return ErrorMessage 错误信息响应
     */
    @ExceptionHandler(DBException.DataNotFound.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage dbException(DBException.DataNotFound ex, WebRequest request) {
        // 记录错误日志
        logger.error("数据异常: ", ex);
        return ErrorMessage.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .description(request.getDescription(false))
                .build();
    }

    /**
     * 处理客户端连接中断异常
     *
     * <p>当客户端提前断开连接时触发，如用户取消请求。
     * 此异常不返回响应，只记录日志。
     *
     * @param ex 客户端中断异常
     * @param handlerMethod 处理方法
     * @param request Web请求对象
     */
    @ExceptionHandler(ClientAbortException.class)
    public void clientAbortException(Exception ex, HandlerMethod handlerMethod, WebRequest request) {
        // 记录客户端中断日志
        logger.error("client abort: class:{} params:{}", handlerMethod.getBeanType(), handlerMethod.getMethodParameters());
    }

    /**
     * 处理运行时异常
     *
     * <p>捕获所有未明确处理的运行时异常。
     *
     * @param ex 运行时异常
     * @param request Web请求对象
     * @return ErrorMessage 错误信息响应
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage runtimeException(RuntimeException ex, WebRequest request) {
        // 记录异常日志
        logger.error("Got ex: ", ex);
        return ErrorMessage.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .description(request.getDescription(false))
                .build();
    }

    /**
     * 处理所有未分类异常
     *
     * <p>作为最后的异常处理兜底，捕获所有其他类型的异常。
     *
     * @param ex 异常对象
     * @param request Web请求对象
     * @return ErrorMessage 错误信息响应
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage globalExceptionHandler(Exception ex, WebRequest request) {
        // 记录异常日志
        logger.error("Got ex: ", ex);
        return ErrorMessage.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .description(request.getDescription(false))
                .build();
    }
}
