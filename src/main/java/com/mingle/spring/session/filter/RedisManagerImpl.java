package com.mingle.spring.session.filter;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;


/**
 * Created by mingle. Time 2019-06-05 12:03 Desc 文件描述
 */
@Component
public class RedisManagerImpl implements RedisManager {
    
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    
    
    @Override
    public void save(String key, Object value, long expire) {
        redisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
    }
    
    
    @Override
    public Object find(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
