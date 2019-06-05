package com.mingle.spring.session.filter;

import java.util.Collections;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionContext;

import lombok.extern.slf4j.Slf4j;


/**
 * Created by mingle. Time 2019-06-05 11:05
 * Desc 适配MapSession到HttpSession
 */
@Slf4j
public class HttpSessionWrapper implements HttpSession {
    
    private MapSession mapSession;
    
    private final ServletContext servletContext;
    
    private boolean invalidated;
    
    private boolean old;
    
    private boolean modified;
    
    
    public HttpSessionWrapper(MapSession mapSession, ServletContext servletContext) {
        this.mapSession = mapSession;
        this.servletContext = servletContext;
    }
    
    
    @Override
    public long getCreationTime() {
        return mapSession.getCreationTime();
    }
    
    
    @Override
    public String getId() {
        return mapSession.getSessionId();
    }
    
    
    @Override
    public long getLastAccessedTime() {
        return mapSession.getLastAccessedTime();
    }
    
    
    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }
    
    
    @Override
    public void setMaxInactiveInterval(int interval) {
        mapSession.setMaxInactiveInterval(interval);
    }
    
    
    @Override
    public int getMaxInactiveInterval() {
        return mapSession.getMaxInactiveInterval();
    }
    
    
    @Override
    public HttpSessionContext getSessionContext() {
        return NOOP_SESSION_CONTEXT;
    }
    
    
    public MapSession getSession() {
        return mapSession;
    }
    
    @Override
    public Object getAttribute(String name) {
        checkState();
        return mapSession.getAttribute(name);
    }
    
    private void checkState() {
        if (this.invalidated) {
            throw new IllegalStateException(
                    "The HttpSession has already be invalidated.");
        }
    }
    
    
    @Override
    public Object getValue(String name) {
        return getAttribute(name);
    }
    
    
    @Override
    public Enumeration<String> getAttributeNames() {
        checkState();
        return Collections.enumeration(mapSession.getAttributeNames());
    }
    
    
    @Override
    public String[] getValueNames() {
        checkState();
        Set<String> attrs = mapSession.getAttributeNames();
        return attrs.toArray(new String[0]);
    }
    
    
    @Override
    public void setAttribute(String name, Object value) {
        checkState();
        modified = true;
        Object oldValue = mapSession.getAttribute(name);
        mapSession.setAttribute(name, value);
    
        if (value != oldValue) {
            if (oldValue instanceof HttpSessionBindingListener) {
                try {
                    ((HttpSessionBindingListener) oldValue).valueUnbound(
                            new HttpSessionBindingEvent(this, name, oldValue));
                }
                catch (Throwable th) {
                    log.error("Error invoking session binding event listener", th);
                }
            }
            if (value instanceof HttpSessionBindingListener) {
                try {
                    ((HttpSessionBindingListener) value)
                            .valueBound(new HttpSessionBindingEvent(this, name, value));
                }
                catch (Throwable th) {
                    log.error("Error invoking session binding event listener", th);
                }
            }
        }
    }
    
    
    @Override
    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }
    
    
    @Override
    public void removeAttribute(String name) {
        checkState();
        modified = true;
        Object oldValue = mapSession.getAttribute(name);
        mapSession.removeAttribute(name);
        if (oldValue instanceof HttpSessionBindingListener) {
            try {
                ((HttpSessionBindingListener) oldValue)
                        .valueUnbound(new HttpSessionBindingEvent(this, name, oldValue));
            }
            catch (Throwable th) {
                log.error("Error invoking session binding event listener", th);
            }
        }
    }
    
    @Override
    public void removeValue(String name) {
        removeAttribute(name);
    }
    
    
    @Override
    public void invalidate() {
        checkState();
        this.invalidated = true;
    }
    
    
    @Override
    public boolean isNew() {
        checkState();
        return !old;
    }
    
    public void setNew(boolean isNew) {
        this.old = !isNew;
    }
    
    public boolean isModified() {
        return modified;
    }
    
    private static final HttpSessionContext NOOP_SESSION_CONTEXT = new HttpSessionContext() {
        
        @Override
        public HttpSession getSession(String sessionId) {
            return null;
        }
        
        @Override
        public Enumeration<String> getIds() {
            return EMPTY_ENUMERATION;
        }
        
    };
    
    private static final Enumeration<String> EMPTY_ENUMERATION = new Enumeration<String>() {
        
        @Override
        public boolean hasMoreElements() {
            return false;
        }
        
        @Override
        public String nextElement() {
            throw new NoSuchElementException("a");
        }
        
    };
}
