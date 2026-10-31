package com.platform.common.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * FastDFS 服务封装类
 * 提供文件上传、下载、删除、防盗链URL生成等功能
 */
@Slf4j
public class FastDfsService {

    private final FdfsProperties properties;

    public FastDfsService(FdfsProperties properties) {
        this.properties = properties;
    }

    /**
     * 上传文件
     *
     * @param fileData 文件数据
     * @param fileExt  文件扩展名(不含点)
     * @return 文件访问路径，格式: group1/M00/00/00/xxx.jpg
     */
    public String uploadFile(byte[] fileData, String fileExt) throws IOException, MyException {
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getTrackerServer();
        StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);

        try {
            String[] results = storageClient.upload_file(fileData, fileExt, null);
            if (results == null || results.length < 2) {
                throw new IOException("Upload file failed, no result returned");
            }

            String groupName = results[0];
            String remoteFileName = results[1];
            return groupName + "/" + remoteFileName;
        } finally {
            closeQuietly(storageServer, trackerServer);
        }
    }

    /**
     * 上传文件(带元数据)
     *
     * @param fileData 文件数据
     * @param fileExt  文件扩展名
     * @param metaData 元数据
     * @return 文件访问路径
     */
    public String uploadFile(byte[] fileData, String fileExt, Map<String, String> metaData)
            throws IOException, MyException {
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getTrackerServer();
        StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);

        try {
            NameValuePair[] pairs = null;
            if (metaData != null && !metaData.isEmpty()) {
                pairs = new NameValuePair[metaData.size()];
                int i = 0;
                for (Map.Entry<String, String> entry : metaData.entrySet()) {
                    pairs[i++] = new NameValuePair(entry.getKey(), entry.getValue());
                }
            }

            String[] results = storageClient.upload_file(fileData, fileExt, pairs);
            if (results == null || results.length < 2) {
                throw new IOException("Upload file failed, no result returned");
            }

            String groupName = results[0];
            String remoteFileName = results[1];
            return groupName + "/" + remoteFileName;
        } finally {
            closeQuietly(storageServer, trackerServer);
        }
    }

    /**
     * 下载文件
     *
     * @param filePath 文件路径，格式: group1/M00/00/00/xxx.jpg
     * @return 文件数据
     */
    public byte[] downloadFile(String filePath) throws IOException, MyException {
        String[] parts = parseFilePath(filePath);
        String groupName = parts[0];
        String remoteFileName = parts[1];

        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getTrackerServer();
        StorageServer storageServer = trackerClient.getStoreStorage(trackerServer, groupName);
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);

        try {
            byte[] data = storageClient.download_file(groupName, remoteFileName);
            if (data == null) {
                throw new IOException("Download file failed, file not found: " + filePath);
            }
            return data;
        } finally {
            closeQuietly(storageServer, trackerServer);
        }
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 是否成功
     */
    public boolean deleteFile(String filePath) throws IOException, MyException {
        String[] parts = parseFilePath(filePath);
        String groupName = parts[0];
        String remoteFileName = parts[1];

        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getTrackerServer();
        StorageClient storageClient = new StorageClient(trackerServer, null);

        try {
            int result = storageClient.delete_file(groupName, remoteFileName);
            return result == 0;
        } finally {
            closeQuietly(null, trackerServer);
        }
    }

    /**
     * 获取文件信息
     *
     * @param filePath 文件路径
     * @return 文件信息
     */
    public FileMetadata getFileInfo(String filePath) throws IOException, MyException {
        String[] parts = parseFilePath(filePath);
        String groupName = parts[0];
        String remoteFileName = parts[1];

        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getTrackerServer();
        StorageClient storageClient = new StorageClient(trackerServer, null);

        try {
            FileInfo info = storageClient.get_file_info(groupName, remoteFileName);
            if (info == null) {
                return null;
            }
            return new FileMetadata(
                    info.getSourceIpAddr(),
                    info.getFileSize(),
                    info.getCreateTimestamp()
            );
        } finally {
            closeQuietly(null, trackerServer);
        }
    }

    private String[] parseFilePath(String filePath) {
        int slashIndex = filePath.indexOf('/');
        if (slashIndex <= 0) {
            throw new IllegalArgumentException("Invalid file path format: " + filePath);
        }
        String groupName = filePath.substring(0, slashIndex);
        String remoteFileName = filePath.substring(slashIndex + 1);
        return new String[]{groupName, remoteFileName};
    }

    private void closeQuietly(Object... resources) {
        for (Object resource : resources) {
            if (resource != null) {
                try {
                    resource.getClass().getMethod("close").invoke(resource);
                } catch (NoSuchMethodException e) {
                    // 该类没有 close 方法，忽略
                } catch (Exception e) {
                    log.warn("Failed to close resource: {}", resource.getClass().getSimpleName(), e);
                }
            }
        }
    }

    /**
     * 文件元数据
     */
    @Data
    public static class FileMetadata {
        private final String sourceIpAddr;
        private final long fileSize;
        private final java.util.Date createTimestamp;
    }

    // ==================== 防盗链URL生成 ====================

    /**
     * 生成带防盗链token的完整访问URL
     *
     * @param filePath 文件路径，格式: group1/M00/00/00/xxx.jpg
     * @return 带token的完整URL，如: http://host/group1/M00/00/00/xxx.jpg?token=xxx&ts=xxx
     */
    public String generateAntiStealUrl(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "";
        }

        // 获取配置
        FdfsProperties.Http http = properties.getHttp();
        if (http == null || !Boolean.TRUE.equals(http.getAntiStealToken())) {
            // 未开启防盗链，直接返回原始URL
            return buildBaseUrl(filePath);
        }

        String secretKey = http.getSecretKey();
        if (secretKey == null || secretKey.isEmpty()) {
            log.warn("防盗链已开启但未配置密钥，返回原始URL");
            return buildBaseUrl(filePath);
        }

        // 生成时间戳（秒）
        int timestamp = (int) (System.currentTimeMillis() / 1000);

        // 生成token: md5(filePath + secretKey + timestamp) 的前8位
        String token = generateToken(filePath, secretKey, timestamp);

        // 构建带token的URL
        String baseUrl = buildBaseUrl(filePath);
        return baseUrl + "?token=" + token + "&ts=" + timestamp;
    }

    /**
     * 构建基础URL（不含token）
     */
    private String buildBaseUrl(String filePath) {
        String webUrlPrefix = properties.getWebUrlPrefix();
        if (webUrlPrefix == null || webUrlPrefix.isEmpty()) {
            return "/" + filePath;
        }
        // 确保URL格式正确
        if (webUrlPrefix.endsWith("/")) {
            return webUrlPrefix + filePath;
        }
        return webUrlPrefix + "/" + filePath;
    }

    /**
     * 生成防盗链token
     * FastDFS防盗链token算法: token = md5(filePath + secretKey + timestamp)
     *
     * @param filePath  文件路径
     * @param secretKey 密钥
     * @param timestamp 时间戳（秒）
     * @return token字符串（8位）
     */
    private String generateToken(String filePath, String secretKey, int timestamp) {
        filePath = filePath.replace("group1/", "");
        String toHash = filePath + secretKey + timestamp;
        return md5Hash(toHash);
    }

    /**
     * MD5哈希计算
     */
    private String md5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            log.error("MD5算法不可用", e);
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }

    /**
     * 字节数组转十六进制字符串
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    /**
     * 检查是否开启了防盗链
     */
    public boolean isAntiStealEnabled() {
        FdfsProperties.Http http = properties.getHttp();
        return http != null && Boolean.TRUE.equals(http.getAntiStealToken());
    }

    /**
     * 获取Web URL前缀
     */
    public String getWebUrlPrefix() {
        return properties.getWebUrlPrefix();
    }
}