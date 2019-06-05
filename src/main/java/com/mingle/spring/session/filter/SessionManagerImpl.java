package com.mingle.spring.session.filter;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;


/**
 * Created by mingle. Time 2019-06-05 11:57 Desc 文件描述
 */
@Component
@Slf4j
public class SessionManagerImpl implements SessionManager {
    
    private String SESSION_KEY = "mingle_session_";
    
    @Resource
    private RedisManager redisManager;
    
    @Override
    public MapSession findById(String key) {
        Object object = redisManager.find(SESSION_KEY + key);
        if (object != null) {
            MapSession session = (MapSession) object;
            if (session.getCreationTime() + session.getMaxInactiveInterval() * 1000 > System.currentTimeMillis()) {
                return session;
            } else {
                log.debug("session {} is timeout but redis not expired", session.getSessionId());
            }
        }
        
        return null;
    }
    
    
    @Override
    public void save(MapSession mapSession) {
        redisManager.save(SESSION_KEY + mapSession.getSessionId(), mapSession, mapSession.getMaxInactiveInterval());
    }
}
