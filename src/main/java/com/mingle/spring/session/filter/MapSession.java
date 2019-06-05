package com.mingle.spring.session.filter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import lombok.Data;


/**
 * Created by mingle. Time 2019-06-05 10:53 Desc 文件描述
 */
@Data
public class MapSession implements Serializable {
    
    private static final long serialVersionUID = -866176937826911452L;
    
    private String sessionId;
    private long creationTime;    //ms
    private long lastAccessedTime;    //ms
    private int maxInactiveInterval; //ms
    
    private Map<String, Object> sessionAttrs = new HashMap<>();
    
    public static MapSession createOne(int maxInactiveInterval) {
        MapSession session = new MapSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setCreationTime(System.currentTimeMillis());
        session.setLastAccessedTime(System.currentTimeMillis());
        session.setMaxInactiveInterval(maxInactiveInterval);
        return session;
    }
    
    public <T> T getAttribute(String attributeName) {
        return (T) this.sessionAttrs.get(attributeName);
    }
    
    
    public Set<String> getAttributeNames() {
        return new HashSet<>(this.sessionAttrs.keySet());
    }
    
    
    public void setAttribute(String attributeName, Object attributeValue) {
        if (attributeValue == null) {
            removeAttribute(attributeName);
        }
        else {
            this.sessionAttrs.put(attributeName, attributeValue);
        }
    }
    
    public void removeAttribute(String attributeName) {
        this.sessionAttrs.remove(attributeName);
    }
}
