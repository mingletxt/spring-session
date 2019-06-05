package com.mingle.spring.session.filter;

import com.mingle.spring.session.cookie.CookieHttpSessionIdResolver;
import com.mingle.spring.session.cookie.HttpSessionIdResolver;
import com.mingle.spring.session.http.TestService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;


/**
 * Created by mingle. Time 2019-06-04 19:34 Desc 文件描述
 */
@Slf4j
@WebFilter(urlPatterns = {"/*"}, filterName = "sessionFilter")
@Order(Integer.MIN_VALUE + 50)
public class SessionFilter extends OncePerRequestFilter {
    
    /**
     * The session repository request attribute name.
     */
    public static final String SESSION_REPOSITORY_ATTR = SessionFilter.class.getName();
    
    /**
     * Invalid session id (not backed by the session repository) request attribute name.
     */
    public static final String INVALID_SESSION_ID_ATTR = SESSION_REPOSITORY_ATTR + ".invalidSessionId";
    
    private static final String CURRENT_SESSION_ATTR = SESSION_REPOSITORY_ATTR + ".CURRENT_SESSION";
    
    @Value("${session.max.interval}")
    private int maxInactiveInterval = 1800;
    
    @Resource
    private SessionManager sessionManager;
    
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        RedisSessionHttpServletRequest redisSessionHttpServletRequest = new RedisSessionHttpServletRequest(request, response);
        RedisSessionHttpServletResponse redisSessionHttpServletResponse = new RedisSessionHttpServletResponse(redisSessionHttpServletRequest, response);
        
        try {
            filterChain.doFilter(redisSessionHttpServletRequest, redisSessionHttpServletResponse);
        } finally {
            redisSessionHttpServletRequest.commitSession();
        }
    }
    
    
    private final class RedisSessionHttpServletRequest extends AbstractHttpServletRequest {
    
        private final HttpServletResponse response;
        
        private boolean requestedSessionCached;
        
        private String requestedSessionId;
        
        private MapSession requestedSession;
        
        private boolean isCommitted;
        
        
        private HttpSessionIdResolver httpSessionIdResolver = new CookieHttpSessionIdResolver();
        
        
        public RedisSessionHttpServletRequest(HttpServletRequest request, HttpServletResponse response) {
            super(request);
            this.response = response;
        }
        
        
        @Override
        public HttpSessionWrapper getSession(boolean create) {
            HttpSessionWrapper currentSession = getCurrentSession();
            if (currentSession != null) {
                return currentSession;
            }
            
            MapSession mapSession = getRequestedSession();
            if (requestedSession != null) {
                currentSession = new HttpSessionWrapper(mapSession, getServletContext());
                currentSession.setNew(false);
                currentSession.getSession().setLastAccessedTime(System.currentTimeMillis());
                setCurrentSession(currentSession);
                return currentSession;
            }else {
                // This is an invalid session id. No need to ask again if
                // request.getSession is invoked for the duration of this request
                if (log.isDebugEnabled()) {
                    log.debug("No session found by id: Caching result for getSession(false) for this HttpServletRequest.");
                }
                setAttribute(INVALID_SESSION_ID_ATTR, "true");
            }
            
            if (!create) {
                return null;
            }
            
            if (log.isDebugEnabled()) {
                log.debug("A new session was created. To help you troubleshoot where the session was created we provided a StackTrace (this is not an error). You can prevent this from appearing by "
                        + "disabling DEBUG logging for " + log.getName(), new RuntimeException("For debugging purposes only (not an error)"));
            }
            MapSession session = MapSession.createOne(maxInactiveInterval);
            currentSession = new HttpSessionWrapper(session, getServletContext());
            setCurrentSession(currentSession);
            return currentSession;
        }
        
        
        private HttpSessionWrapper getCurrentSession() {
            return (HttpSessionWrapper) getAttribute(CURRENT_SESSION_ATTR);
        }
        
        
        private void setCurrentSession(HttpSession currentSession) {
            if (currentSession == null) {
                removeAttribute(CURRENT_SESSION_ATTR);
            }else {
                setAttribute(CURRENT_SESSION_ATTR, currentSession);
            }
        }
        
        
        private MapSession getRequestedSession() {
            if (!this.requestedSessionCached) {
                List<String> sessionIds = httpSessionIdResolver.resolveSessionIds(this);
                for (String sessionId : sessionIds) {
                    if (this.requestedSessionId == null) {
                        this.requestedSessionId = sessionId;
                    }
                    MapSession mapSession = sessionManager.findById(sessionId);
                    if (mapSession != null) {
                        this.requestedSession = mapSession;
                        this.requestedSessionId = sessionId;
                        break;
                    }
                }
                this.requestedSessionCached = true;
            }
            return this.requestedSession;
        }
        
        
        @Override
        public HttpSession getSession() {
            return getSession(true);
        }
        
        
        private void commitSession() {
            if (isCommitted) {
                log.debug("session has been committed already");
                return;
            }
            
            HttpSessionWrapper httpSessionWrapper = getCurrentSession();
            if (httpSessionWrapper != null && httpSessionWrapper.isModified()) {
                MapSession session = httpSessionWrapper.getSession();
                sessionManager.save(session);
                httpSessionIdResolver.setSessionId(this, response, session.getSessionId());
            }
            isCommitted = true;
        }
        
        
        @Override
        public String changeSessionId() {
            return request.changeSessionId();
        }
        
        
        @Override
        public boolean isRequestedSessionIdValid() {
            return request.isRequestedSessionIdValid();
        }
        
        
        @Override
        public boolean isRequestedSessionIdFromCookie() {
            return request.isRequestedSessionIdFromCookie();
        }
        
        
        @Override
        public boolean isRequestedSessionIdFromURL() {
            return request.isRequestedSessionIdFromURL();
        }
        
        
        @Override
        public boolean isRequestedSessionIdFromUrl() {
            return request.isRequestedSessionIdFromUrl();
        }
        
        
        @Override
        public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
            return request.authenticate(response);
        }
    }
    
    
    private final class RedisSessionHttpServletResponse extends OnCommittedResponseWrapper {
        
        private final RedisSessionHttpServletRequest request;
        
        
        /**
         * Create a new {@link RedisSessionHttpServletResponse}.
         *
         * @param request the request to be wrapped
         * @param response the response to be wrapped
         */
        RedisSessionHttpServletResponse(RedisSessionHttpServletRequest request, HttpServletResponse response) {
            super(response);
            if (request == null) {
                throw new IllegalArgumentException("request cannot be null");
            }
            this.request = request;
        }
        
        
        @Override
        protected void onResponseCommitted() {
            this.request.commitSession();
        }
    }
    
}
