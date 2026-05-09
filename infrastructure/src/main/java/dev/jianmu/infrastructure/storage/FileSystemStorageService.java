package dev.jianmu.infrastructure.storage;

import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

/**
 * FileSystemStorageService - 本地文件系统存储服务实现
 *
 * <p>该类使用本地文件系统作为存储后端。
 * 适用于单机部署或需要直接访问文件系统的场景。
 *
 * <p>特性：
 * <ul>
 *   <li>简单易用：无需额外依赖</li>
 *   <li>直接访问：文件直接存储在本地磁盘</li>
 *   <li>高性能：适合大文件存储</li>
 * </ul>
 *
 * <p>注意：
 * <ul>
 *   <li>不适合多节点共享存储</li>
 *   <li>需要考虑磁盘空间管理</li>
 *   <li>生产环境建议使用云存储或NFS</li>
 * </ul>
 *
 * @author Daihw
 * @see StorageService
 */
@Service
public class FileSystemStorageService implements StorageService {

    /**
     * 存储根目录
     */
    private final Path rootLocation;

    /**
     * 构造函数
     *
     * @param properties 存储配置
     */
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getBaseDir());
        init();
    }

    /**
     * 初始化存储目录
     */
    private void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    /**
     * 存储文件
     *
     * @param path 文件路径（相对于根目录）
     * @param content 文件内容
     */
    @Override
    public void store(String path, byte[] content) {
        try {
            Path destinationFile = rootLocation.resolve(path).normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(rootLocation.toAbsolutePath())) {
                throw new StorageException("Cannot store file outside current directory.");
            }

            Files.createDirectories(destinationFile.getParent());
            Files.write(destinationFile, content);
        } catch (IOException e) {
            throw new StorageException("Failed to store file", e);
        }
    }

    /**
     * 读取文件
     *
     * @param path 文件路径
     * @return 文件内容
     */
    @Override
    public byte[] load(String path) {
        try {
            Path file = rootLocation.resolve(path).normalize().toAbsolutePath();
            return Files.readAllBytes(file);
        } catch (IOException e) {
            throw new StorageFileNotFoundException(path);
        }
    }

    /**
     * 删除文件
     *
     * @param path 文件路径
     */
    @Override
    public void delete(String path) {
        try {
            Path file = rootLocation.resolve(path).normalize().toAbsolutePath();
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new StorageException("Failed to delete file", e);
        }
    }

    /**
     * 检查文件是否存在
     *
     * @param path 文件路径
     * @return 是否存在
     */
    @Override
    public boolean exists(String path) {
        Path file = rootLocation.resolve(path).normalize().toAbsolutePath();
        return Files.exists(file);
    }

    /**
     * 获取文件URL
     *
     * <p>本地存储返回文件路径或相对URL。
     *
     * @param path 文件路径
     * @return 文件URL
     */
    @Override
    public String getUrl(String path) {
        return "/" + path;
    }

    /**
     * 读取文件流
     *
     * @param path 文件路径
     * @return 文件输入流
     */
    public InputStream loadAsStream(String path) {
        try {
            Path file = rootLocation.resolve(path).normalize().toAbsolutePath();
            return Files.newInputStream(file);
        } catch (IOException e) {
            throw new StorageFileNotFoundException(path);
        }
    }

    /**
     * 列出目录下的所有文件
     *
     * @param dir 目录路径
     * @return 文件流
     */
    public Stream<Path> loadAll(String dir) {
        try {
            Path directory = rootLocation.resolve(dir);
            return Files.walk(directory, 1)
                    .filter(path -> !path.equals(directory))
                    .map(path -> rootLocation.relativize(path));
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }
    }

    /**
     * 删除所有文件
     */
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation);
    }
}
