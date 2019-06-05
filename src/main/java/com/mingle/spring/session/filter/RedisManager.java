package com.mingle.spring.session.filter;

/**
 * Created by mingle. Time 2019-06-05 11:58 Desc 文件描述
 */
public interface RedisManager {
    
    /**
     * 保存
     *
     * @param key
     * @param value
     * @param expire 秒
     */
    void save(String key, Object value, long expire);
    
    /**
     * 查找
     *
     * @param key
     * @return
     */
    Object find(String key);
    
}
