package com.platform.common.service;

import com.platform.common.constants.RedisConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * Redis布隆过滤器服务
 * 基于Redis Bitmap实现，支持多实例共享
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BloomFilterService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 初始化布隆过滤器
     */
    public void init(String bloomName, long expectedItems, double falseProbability, Collection<String> items) {
        String redisKey = RedisConstants.BLOOM_FILTER_KEY_PREFIX + bloomName;

        // 计算最优参数
        long bitSize = optimalBitSize(expectedItems, falseProbability);
        int hashCount = optimalHashCount(bitSize, expectedItems);

        // 存储元数据
        redisTemplate.opsForHash().put(redisKey, "bitSize", bitSize);
        redisTemplate.opsForHash().put(redisKey, "hashCount", hashCount);
        redisTemplate.opsForHash().put(redisKey, "expectedItems", expectedItems);
        redisTemplate.opsForHash().put(redisKey, "falseProbability", falseProbability);

        // 批量添加初始元素
        if (items != null && !items.isEmpty()) {
            addAll(bloomName, items);
        }

        log.info("布隆过滤器初始化完成: {}, bitSize={}, hashCount={}, items={}",
                bloomName, bitSize, hashCount, items != null ? items.size() : 0);
    }

    public void add(String bloomName, String item) {
        if (item == null) {
            return;
        }

        String redisKey = RedisConstants.BLOOM_FILTER_KEY_PREFIX + bloomName;
        long bitSize = getBitSize(redisKey);
        int hashCount = getHashCount(redisKey);

        for (int i = 0; i < hashCount; i++) {
            long hash = hash(item, i);
            long bitIndex = Math.abs(hash % bitSize);
            redisTemplate.opsForValue().setBit(redisKey + ":bits", bitIndex, true);
        }
    }

    /**
     * 批量添加元素
     */
    public void addAll(String bloomName, Collection<String> items) {
        if (items == null) {
            return;
        }
        items.forEach(item -> add(bloomName, item));
    }

    public boolean mightContain(String bloomName, String item) {
        if (item == null) {
            return false;
        }

        String redisKey = RedisConstants.BLOOM_FILTER_KEY_PREFIX + bloomName;
        long bitSize = getBitSize(redisKey);
        int hashCount = getHashCount(redisKey);

        for (int i = 0; i < hashCount; i++) {
            long hash = hash(item, i);
            long bitIndex = Math.abs(hash % bitSize);
            Boolean bit = redisTemplate.opsForValue().getBit(redisKey + ":bits", bitIndex);
            if (!Boolean.TRUE.equals(bit)) {
                return false;
            }
        }
        return true;
    }

    public void delete(String bloomName) {
        String redisKey = RedisConstants.BLOOM_FILTER_KEY_PREFIX + bloomName;
        redisTemplate.delete(redisKey);
        redisTemplate.delete(redisKey + ":bits");
        log.info("布隆过滤器已删除: {}", bloomName);
    }

    private long getBitSize(String redisKey) {
        Object value = redisTemplate.opsForHash().get(redisKey, "bitSize");
        return Long.parseLong(value.toString());
    }

    private int getHashCount(String redisKey) {
        Object value = redisTemplate.opsForHash().get(redisKey, "hashCount");
        if (value == null) {
            return 5;
        }
        return Integer.parseInt(value.toString());
    }

    private long optimalBitSize(long n, double p) {
        return (long) Math.ceil(-n * Math.log(p) / Math.pow(Math.log(2), 2));
    }

    private int optimalHashCount(long m, long n) {
        return (int) Math.ceil((double) m / n * Math.log(2));
    }

    private long hash(String item, int seed) {
        long hash = 0;
        for (int i = 0; i < item.length(); i++) {
            hash = hash * 31 + item.charAt(i) + seed * 17;
        }
        return hash;
    }
}