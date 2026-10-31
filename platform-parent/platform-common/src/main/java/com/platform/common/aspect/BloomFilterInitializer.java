package com.platform.common.aspect;

import com.platform.common.annotation.BloomCheck;
import com.platform.common.service.BloomFilterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 布隆过滤器初始化器
 * 启动时自动初始化标记 autoInit=true 的布隆过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BloomFilterInitializer implements ApplicationRunner {

    private final ApplicationContext applicationContext;
    private final BloomFilterService bloomFilterService;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        log.info("开始初始化布隆过滤器...");
        Map<String, BloomInitConfig> configMap = new HashMap<>();
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            Class<?> beanClass = bean.getClass();

            ReflectionUtils.doWithMethods(beanClass, method -> {
                BloomCheck bloomCheck = AnnotationUtils.findAnnotation(method, BloomCheck.class);
                if (bloomCheck != null) {
                    String bloomName = bloomCheck.bloomName();
                    if (!configMap.containsKey(bloomName)) {
                        configMap.put(bloomName, new BloomInitConfig(
                                bloomName,
                                bloomCheck.initQuery(),
                                bloomCheck.expectedItems(),
                                bloomCheck.falseProbability()
                        ));
                    }
                }
            });
        }

        for (BloomInitConfig config : configMap.values()) {
            try {
                initBloomFilter(config);
            } catch (Exception e) {
                log.error("布隆过滤器初始化失败: {}", config.bloomName, e);
            }
        }

        log.info("布隆过滤器初始化完成，共初始化 {} 个", configMap.size());
    }

    private void initBloomFilter(BloomInitConfig config) {
        if (config.initQuery == null || config.initQuery.isEmpty()) {
            log.warn("布隆过滤器 {} 未配置initQuery，跳过初始化", config.bloomName);
            return;
        }

        List<String> items = jdbcTemplate.queryForList(config.initQuery, String.class);

        if (items.isEmpty()) {
            log.warn("布隆过滤器 {} 初始化数据为空", config.bloomName);
        }

        // 使用配置中的参数初始化
        bloomFilterService.init(config.bloomName, config.expectedItems, config.falseProbability, items);

        log.info("布隆过滤器 {} 初始化成功，数据量: {}, 预期元素: {}, 误判率: {}",
                config.bloomName, items.size(), config.expectedItems, config.falseProbability);
    }

    private static class BloomInitConfig {
        final String bloomName;
        final String initQuery;
        final long expectedItems;
        final double falseProbability;

        BloomInitConfig(String bloomName, String initQuery, long expectedItems, double falseProbability) {
            this.bloomName = bloomName;
            this.initQuery = initQuery;
            this.expectedItems = expectedItems;
            this.falseProbability = falseProbability;
        }
    }
}