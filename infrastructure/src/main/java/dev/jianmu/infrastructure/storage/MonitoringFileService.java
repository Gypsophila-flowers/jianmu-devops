package dev.jianmu.infrastructure.storage;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * MonitoringFileService - 监控文件服务
 *
 * <p>该类提供日志文件的实时读取功能。
 * 支持文件追加和流式读取，常用于实时日志展示。
 *
 * <p>功能特点：
 * <ul>
 *   <li>流式读取：支持大文件的渐进式读取</li>
 *   <li>追加检测：能够检测文件的新增内容</li>
 *   <li>位置跟踪：跟踪当前读取位置</li>
 * </ul>
 *
 * @author Daihw
 */
public class MonitoringFileService {

    /**
     * 读取文件内容
     *
     * @param fileStream 文件输入流
     * @param startPosition 起始位置
     * @param maxBytes 最大读取字节数
     * @return 读取的内容
     * @throws Exception 读取异常
     */
    public static byte[] readFile(InputStream fileStream, long startPosition, int maxBytes) throws Exception {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        long skipped = 0;

        while (skipped < startPosition) {
            long n = fileStream.skip(startPosition - skipped);
            if (n <= 0) break;
            skipped += n;
        }

        int bytesRead;
        int totalRead = 0;
        while (totalRead < maxBytes && (bytesRead = fileStream.read(buffer, 0, Math.min(buffer.length, maxBytes - totalRead))) != -1) {
            result.write(buffer, 0, bytesRead);
            totalRead += bytesRead;
        }

        return result.toByteArray();
    }

    /**
     * 获取文件的最后N行
     *
     * @param content 文件内容
     * @param maxLines 最大行数
     * @return 最后N行内容
     */
    public static String getLastLines(String content, int maxLines) {
        String[] lines = content.split("\n");
        if (lines.length <= maxLines) {
            return content;
        }

        StringBuilder result = new StringBuilder();
        for (int i = lines.length - maxLines; i < lines.length; i++) {
            result.append(lines[i]).append("\n");
        }
        return result.toString();
    }
}
