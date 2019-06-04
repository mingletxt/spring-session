package com.mingle.spring.session.filter;

import com.mingle.spring.session.http.TestService;

import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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
@WebFilter(urlPatterns = {"/hello/*"}, filterName = "sessionFilter")
@Order(Integer.MIN_VALUE + 50)
public class SessionFilter extends OncePerRequestFilter {
    
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
}
