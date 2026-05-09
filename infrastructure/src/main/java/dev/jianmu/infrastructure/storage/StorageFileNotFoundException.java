package dev.jianmu.infrastructure.storage;

/**
 * StorageFileNotFoundException - 存储文件未找到异常类
 *
 * <p>该类表示请求的文件在存储系统中不存在。
 * 通常在读取文件或下载文件时抛出。
 *
 * @author Daihw
 */
public class StorageFileNotFoundException extends StorageException {

    /**
     * 文件路径
     */
    private final String filePath;

    /**
     * 构造函数
     *
     * @param filePath 文件路径
     */
    public StorageFileNotFoundException(String filePath) {
        super("File not found: " + filePath);
        this.filePath = filePath;
    }

    /**
     * 获取文件路径
     *
     * @return 文件路径
     */
    public String getFilePath() {
        return filePath;
    }
}
