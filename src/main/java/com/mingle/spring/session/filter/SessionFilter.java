package com.mingle.spring.session.filter;

import com.mingle.spring.session.cookie.CookieHttpSessionIdResolver;
import com.mingle.spring.session.cookie.HttpSessionIdResolver;
import com.mingle.spring.session.http.TestService;

import org.springframework.core.annotation.Order;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.time.Instant;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import lombok.extern.slf4j.Slf4j;


/**
 * Created by mingle. Time 2019-06-04 19:34 Desc 文件描述
 */
@Slf4j
@WebFilter(urlPatterns = {"/hello/*"}, filterName = "sessionFilter")
@Order(Integer.MIN_VALUE + 50)
public class SessionFilter extends OncePerRequestFilter {
    
    /**
     * The session repository request attribute name.
     */
    public static final String SESSION_REPOSITORY_ATTR = SessionFilter.class
            .getName();
    
    /**
     * Invalid session id (not backed by the session repository) request attribute name.
     */
    public static final String INVALID_SESSION_ID_ATTR = SESSION_REPOSITORY_ATTR
            + ".invalidSessionId";
    
    private static final String CURRENT_SESSION_ATTR = SESSION_REPOSITORY_ATTR
            + ".CURRENT_SESSION";
    
    @Resource
    private TestService testService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("hello filter {}", testService.getTime());
        HttpSession session = request.getSession();
        session.setAttribute("name", "mingle");
        RedisSessionHttpServletRequest redisSessionHttpServletRequest = new RedisSessionHttpServletRequest(request);
        filterChain.doFilter(redisSessionHttpServletRequest, response);
    }
    
    private final class RedisSessionHttpServletRequest implements HttpServletRequest {
        
        private HttpServletRequest request;
        
        private boolean requestedSessionCached;
        
        private String requestedSessionId;
        
        private HttpSession requestedSession;
        
        private Boolean requestedSessionIdValid;
        
        private HttpSessionIdResolver httpSessionIdResolver = new CookieHttpSessionIdResolver();
        
        public RedisSessionHttpServletRequest(HttpServletRequest request) {
            this.request = request;
        }
        
        @Override
        public Object getAttribute(String name) {
            return request.getAttribute(name);
        }
        
        
        @Override
        public Enumeration<String> getAttributeNames() {
            return request.getAttributeNames();
        }
        
        
        @Override
        public String getCharacterEncoding() {
            return request.getCharacterEncoding();
        }
        
        
        @Override
        public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
            request.setCharacterEncoding(env);
        }
        
        
        @Override
        public int getContentLength() {
            return request.getContentLength();
        }
        
        
        @Override
        public long getContentLengthLong() {
            return request.getContentLengthLong();
        }
        
        
        @Override
        public String getContentType() {
            return request.getContentType();
        }
        
        
        @Override
        public ServletInputStream getInputStream() throws IOException {
            return request.getInputStream();
        }
        
        
        @Override
        public String getParameter(String name) {
            return request.getParameter(name);
        }
        
        
        @Override
        public Enumeration<String> getParameterNames() {
            return request.getParameterNames();
        }
        
        
        @Override
        public String[] getParameterValues(String name) {
            return request.getParameterValues(name);
        }
        
        
        @Override
        public Map<String, String[]> getParameterMap() {
            return request.getParameterMap();
        }
        
        
        @Override
        public String getProtocol() {
            return request.getProtocol();
        }
        
        
        @Override
        public String getScheme() {
            return request.getScheme();
        }
        
        
        @Override
        public String getServerName() {
            return request.getServerName();
        }
        
        
        @Override
        public int getServerPort() {
            return request.getServerPort();
        }
        
        
        @Override
        public BufferedReader getReader() throws IOException {
            return request.getReader();
        }
        
        
        @Override
        public String getRemoteAddr() {
            return request.getRemoteAddr();
        }
        
        
        @Override
        public String getRemoteHost() {
            return request.getRemoteHost();
        }
        
        
        @Override
        public void setAttribute(String name, Object o) {
            request.setAttribute(name, o);
        }
        
        
        @Override
        public void removeAttribute(String name) {
            request.removeAttribute(name);
        }
        
        
        @Override
        public Locale getLocale() {
            return request.getLocale();
        }
        
        
        @Override
        public Enumeration<Locale> getLocales() {
            return request.getLocales();
        }
        
        
        @Override
        public boolean isSecure() {
            return request.isSecure();
        }
        
        
        @Override
        public RequestDispatcher getRequestDispatcher(String path) {
            return request.getRequestDispatcher(path);
        }
        
        
        @Override
        public String getRealPath(String path) {
            return request.getRealPath(path);
        }
        
        
        @Override
        public int getRemotePort() {
            return request.getRemotePort();
        }
        
        
        @Override
        public String getLocalName() {
            return request.getLocalName();
        }
        
        
        @Override
        public String getLocalAddr() {
            return request.getLocalAddr();
        }
        
        
        @Override
        public int getLocalPort() {
            return request.getLocalPort();
        }
        
        
        @Override
        public ServletContext getServletContext() {
            return request.getServletContext();
        }
        
        
        @Override
        public AsyncContext startAsync() throws IllegalStateException {
            return request.startAsync();
        }
        
        
        @Override
        public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
            return request.startAsync(servletRequest, servletResponse);
        }
        
        
        @Override
        public boolean isAsyncStarted() {
            return request.isAsyncStarted();
        }
        
        
        @Override
        public boolean isAsyncSupported() {
            return request.isAsyncSupported();
        }
        
        
        @Override
        public AsyncContext getAsyncContext() {
            return request.getAsyncContext();
        }
        
        
        @Override
        public DispatcherType getDispatcherType() {
            return request.getDispatcherType();
        }
        
        
        @Override
        public String getAuthType() {
            return request.getAuthType();
        }
        
        
        @Override
        public Cookie[] getCookies() {
            return request.getCookies();
        }
        
        
        @Override
        public long getDateHeader(String name) {
            return request.getDateHeader(name);
        }
        
        
        @Override
        public String getHeader(String name) {
            return request.getHeader(name);
        }
        
        
        @Override
        public Enumeration<String> getHeaders(String name) {
            return request.getHeaders(name);
        }
        
        
        @Override
        public Enumeration<String> getHeaderNames() {
            return request.getHeaderNames();
        }
        
        
        @Override
        public int getIntHeader(String name) {
            return request.getIntHeader(name);
        }
        
        
        @Override
        public String getMethod() {
            return request.getMethod();
        }
        
        
        @Override
        public String getPathInfo() {
            return request.getPathInfo();
        }
        
        
        @Override
        public String getPathTranslated() {
            return request.getPathTranslated();
        }
        
        
        @Override
        public String getContextPath() {
            return request.getContextPath();
        }
        
        
        @Override
        public String getQueryString() {
            return request.getQueryString();
        }
        
        
        @Override
        public String getRemoteUser() {
            return request.getRemoteUser();
        }
        
        
        @Override
        public boolean isUserInRole(String role) {
            return request.isUserInRole(role);
        }
        
        
        @Override
        public Principal getUserPrincipal() {
            return request.getUserPrincipal();
        }
        
        
        @Override
        public String getRequestedSessionId() {
            return request.getRequestedSessionId();
        }
        
        
        @Override
        public String getRequestURI() {
            return request.getRequestURI();
        }
        
        
        @Override
        public StringBuffer getRequestURL() {
            return request.getRequestURL();
        }
        
        
        @Override
        public String getServletPath() {
            return request.getServletPath();
        }
        
        
        @Override
        public HttpSession getSession(boolean create) {
            HttpSession currentSession = getCurrentSession();
            if (currentSession != null) {
                return currentSession;
            }
            
            HttpSession requestedSession = getRequestedSession();
            if (requestedSession != null) {
                this.requestedSessionIdValid = true;
                setCurrentSession(requestedSession);
                return requestedSession;
            } else {
                // This is an invalid session id. No need to ask again if
                // request.getSession is invoked for the duration of this request
                if (log.isDebugEnabled()) {
                    log.debug(
                            "No session found by id: Caching result for getSession(false) for this HttpServletRequest.");
                }
                setAttribute(INVALID_SESSION_ID_ATTR, "true");
            }
    
            if (!create) {
                return null;
            }
    
            if (log.isDebugEnabled()) {
                log.debug(
                        "A new session was created. To help you troubleshoot where the session was created we provided a StackTrace (this is not an error). You can prevent this from appearing by disabling DEBUG logging for "
                                + log.getName(),
                        new RuntimeException(
                                "For debugging purposes only (not an error)"));
            }
            HttpSession session = SessionRepositoryFilter.this.sessionRepository.createSession();
            setCurrentSession(session);
            return session;
        }
        
        private HttpSession getCurrentSession() {
            return (HttpSession) getAttribute(CURRENT_SESSION_ATTR);
        }
        
        private void setCurrentSession(HttpSession currentSession) {
            if (currentSession == null) {
                removeAttribute(CURRENT_SESSION_ATTR);
            }
            else {
                setAttribute(CURRENT_SESSION_ATTR, currentSession);
            }
        }
        
        private HttpSession getRequestedSession() {
            if (!this.requestedSessionCached) {
                List<String> sessionIds = this.httpSessionIdResolver
                        .resolveSessionIds(this);
                for (String sessionId : sessionIds) {
                    if (this.requestedSessionId == null) {
                        this.requestedSessionId = sessionId;
                    }
                    HttpSession session = SessionRepositoryFilter.this.sessionRepository
                            .findById(sessionId);
                    if (session != null) {
                        this.requestedSession = session;
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
        
        
        @Override
        public void login(String username, String password) throws ServletException {
            request.login(username, password);
        }
        
        
        @Override
        public void logout() throws ServletException {
            request.logout();
        }
        
        
        @Override
        public Collection<Part> getParts() throws IOException, ServletException {
            return request.getParts();
        }
        
        
        @Override
        public Part getPart(String name) throws IOException, ServletException {
            return request.getPart(name);
        }
        
        
        @Override
        public <T extends HttpUpgradeHandler> T upgrade(Class<T> httpUpgradeHandlerClass) throws IOException, ServletException {
            return request.upgrade(httpUpgradeHandlerClass);
        }
    }
}
