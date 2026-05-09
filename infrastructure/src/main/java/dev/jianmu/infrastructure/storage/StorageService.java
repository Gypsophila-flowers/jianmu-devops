package dev.jianmu.infrastructure.storage;

/**
 * StorageService - 存储服务接口
 *
 * <p>该接口定义了文件存储的统一操作。
 * 支持本地存储和云存储等多种后端。
 *
 * <p>主要功能：
 * <ul>
 *   <li>文件存储：保存文件到存储系统</li>
 *   <li>文件读取：获取存储的文件</li>
 *   <li>文件删除：删除不再需要的文件</li>
 *   <li>文件列表：列出目录下的文件</li>
 * </ul>
 *
 * <p>实现类：
 * <ul>
 *   <li>{@link FileSystemStorageService} - 本地文件系统存储</li>
 *   <li>其他云存储实现</li>
 * </ul>
 *
 * @author Daihw
 */
public interface StorageService {

    /**
     * 存储文件
     *
     * @param path 文件路径
     * @param content 文件内容
     */
    void store(String path, byte[] content);

    /**
     * 读取文件
     *
     * @param path 文件路径
     * @return 文件内容
     */
    byte[] load(String path);

    /**
     * 删除文件
     *
     * @param path 文件路径
     */
    void delete(String path);

    /**
     * 检查文件是否存在
     *
     * @param path 文件路径
     * @return 是否存在
     */
    boolean exists(String path);

    /**
     * 获取文件URL
     *
     * @param path 文件路径
     * @return 文件URL
     */
    String getUrl(String path);
}
