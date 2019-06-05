package com.mingle.spring.session.filter;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;


/**
 * Created by mingle. Time 2019-06-05 12:19 Desc 文件描述
 */
//@Component
public class MemoryManager implements RedisManager {
    
    private final Map<String, Object> map = new HashMap<>();
    
    @Override
    public void save(String key, Object value, long expire) {
        map.put(key, value);
    }
    
    
    @Override
    public Object find(String key) {
        return map.get(key);
    }
}
