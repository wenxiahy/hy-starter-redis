package com.wenxiahy.hy.redis.service;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author zhouw
 * @Description
 * @Date 2020-12-18
 */
public class RedisService {

    private RedisTemplate<String, Object> redisTemplate;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public boolean deleteKey(String key) {
        return redisTemplate.delete(key);
    }

    public long deleteKey(String... keys) {
        Set<String> keySet = Stream.of(keys).map(k -> k).collect(Collectors.toSet());
        return redisTemplate.delete(keySet);
    }

    public long deleteKey(Collection<String> keys) {
        Set<String> keySet = keys.stream().map(k -> k).collect(Collectors.toSet());
        return redisTemplate.delete(keySet);
    }

    /**
     * 设置过期时间
     *
     * @param key
     * @param timeout 单位：秒
     * @return
     */
    public boolean expire(String key, long timeout) {
        return redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 指定key在某一个时间过期
     *
     * @param key
     * @param date
     * @return
     */
    public boolean expireAt(String key, Date date) {
        return redisTemplate.expireAt(key, date);
    }

    /**
     * 获取过期时间
     *
     * @param key
     * @return 时间（秒），返回0表示永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 将key设置为永久有效
     *
     * @param key
     * @return
     */
    public boolean persist(String key) {
        return redisTemplate.persist(key);
    }

    /**
     * 普通缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 普通缓存
     *
     * @param key
     * @param value
     * @param timeout 单位：秒
     * @return
     */
    public boolean set(String key, Object value, long timeout) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Object get(String key, boolean delete) {
        Object o = get(key);
        if (delete) {
            deleteKey(key);
        }

        return o;
    }

    /**
     * HashGet
     *
     * @param key
     * @param item
     * @return
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 向一张hash表中放数据，如果不存在则创建
     *
     * @param key
     * @param item
     * @param value
     */
    public void hset(String key, String item, Object value) {
        redisTemplate.opsForHash().put(key, item, value);
    }

    /**
     * 获取key对应的所有健值对
     *
     * @param key
     * @return
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     *
     * @param key
     * @param map
     */
    public void hmset(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 删除hash表中的值
     *
     * @param key
     * @param items
     */
    public long hdelete(String key, String... items) {
        return redisTemplate.opsForHash().delete(key, items);
    }

    /**
     * 判断hash表中是否存在
     *
     * @param key
     * @param item
     * @return
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * 递增
     *
     * @param key
     * @param value
     * @return
     */
    public long incr(String key, long value) {
        if (value <= 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, value);
    }

    /**
     * 递减
     *
     * @param key
     * @param value
     * @return
     */
    public long decr(String key, long value) {
        if (value <= 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -value);
    }

    /**
     * 判断set中是否存在
     *
     * @param key
     * @param value
     * @return
     */
    public boolean sHasKey(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 将数据放入set中
     *
     * @param key
     * @param values
     * @return
     */
    public long sset(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * 取出set中所有的数据
     *
     * @param key
     * @return
     */
    public Set<Object> sget(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 从左边放入list
     *
     * @param key
     */
    public void llset(String key, Object value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 从右边放入list
     *
     * @param key
     * @param value
     */
    public void rlset(String key, Object value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * list的大小
     *
     * @param key
     * @return
     */
    public long lsize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 获取list中的数据，0到-1表示取所有的值
     *
     * @param key
     * @param start 开始
     * @param end   结束
     * @return
     */
    public List<Object> lget(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 获取list中所有的数据，如果数据量很大，建议分批获取
     *
     * @param key
     * @return
     */
    public List<Object> lget(String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 通过index获取list中的数据
     *
     * @param key
     * @param index 0：第一个，1：第二个，-1：最后一个，-2：倒数第二个，依次类推
     * @return
     */
    public Object lget(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }
}
