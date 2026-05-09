package dev.jianmu.infrastructure.storage;

/**
 * StorageProperties - 存储配置属性类
 *
 * <p>该类用于加载和访问文件存储相关的配置项。
 *
 * <p>配置项说明：
 * <ul>
 *   <li>base-dir - 存储根目录</li>
 *   <li>type - 存储类型（local/minio/aliyun-oss）</li>
 *   <li>max-file-size - 最大文件大小</li>
 * </ul>
 *
 * @author Daihw
 */
public class StorageProperties {

    /**
     * 存储根目录
     */
    private String baseDir;

    /**
     * 存储类型
     */
    private String type = "local";

    /**
     * 最大文件大小（字节）
     */
    private long maxFileSize = 100 * 1024 * 1024;

    public String getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }
}
