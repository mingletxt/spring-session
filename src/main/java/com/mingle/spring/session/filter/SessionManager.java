package com.mingle.spring.session.filter;

/**
 * Created by mingle. Time 2019-06-05 11:55 Desc 文件描述
 */
public interface SessionManager {
    
    MapSession findById(String id);
    
    void save(MapSession mapSession);
    
}
