package com.platform.common.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.TrackerClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * FastDFS配置 (使用官方原始客户端 org.csource:fastdfs-client-java)
 * <p>
 * 兼容 Spring Boot 3.x，不依赖 javax 包
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "fdfs.enabled", havingValue = "true")
public class FastDfsConfig {

    private final FdfsProperties properties;

    public FastDfsConfig(FdfsProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        try {
            log.info("Initializing FastDFS client...");
            StringBuilder trackerServers = new StringBuilder();
            for (String tracker : properties.getTrackerList()) {
                if (!trackerServers.isEmpty()) {
                    trackerServers.append(",");
                }
                trackerServers.append(tracker);
            }

            ClientGlobal.initByTrackers(trackerServers.toString());
            ClientGlobal.setG_connect_timeout(properties.getConnectTimeout());
            ClientGlobal.setG_network_timeout(properties.getSoTimeout());

            log.info("FastDFS client initialized successfully. Trackers: {}", trackerServers);
        } catch (Exception e) {
            log.error("Failed to initialize FastDFS client", e);
            throw new RuntimeException("Failed to initialize FastDFS client", e);
        }
    }

    @PreDestroy
    public void destroy() {
        log.info("FastDFS client destroyed");
    }

    @Bean
    public TrackerClient trackerClient() {
        return new TrackerClient();
    }

    @Bean
    public FastDfsService fastDfsService() {
        return new FastDfsService(properties);
    }
}

@Data
@Configuration
@ConfigurationProperties(prefix = "fdfs")
class FdfsProperties {
    /**
     * Tracker服务器地址列表
     */
    private Set<String> trackerList;

    /**
     * socket超时时间(毫秒)
     */
    private Integer soTimeout = 1500;

    /**
     * 连接超时时间(毫秒)
     */
    private Integer connectTimeout = 600;

    /**
     * 缩略图配置(暂未实现)
     */
    private ThumbImage thumbImage;

    /**
     * HTTP配置
     */
    private Http http;

    /**
     * FastDFS文件访问URL前缀
     * 例如: http://img.platformai.com 或 http://127.0.0.1:8888
     */
    private String webUrlPrefix = "";

    @Data
    public static class ThumbImage {
        private Integer width = 150;
        private Integer height = 150;
    }

    @Data
    public static class Http {
        /**
         * 是否开启防盗链
         */
        private Boolean antiStealToken = false;
        /**
         * 防盗链密钥
         */
        private String secretKey;
        /**
         * Tracker HTTP端口
         */
        private Integer trackerHttpPort = 80;
    }
}