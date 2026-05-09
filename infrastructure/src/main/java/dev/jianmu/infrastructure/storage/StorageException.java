package dev.jianmu.infrastructure.storage;

/**
 * StorageException - 存储异常类
 *
 * <p>该类是存储操作相关异常的基类。
 * 用于封装文件存储操作中发生的各种错误。
 *
 * @author Daihw
 */
public class StorageException extends RuntimeException {

    /**
     * 构造函数
     *
     * @param message 错误消息
     */
    public StorageException(String message) {
        super(message);
    }

    /**
     * 构造函数
     *
     * @param message 错误消息
     * @param cause 原始异常
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
