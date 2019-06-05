package com.mingle.spring.session.http;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;


/**
 * Created by mingle. Time 2019-06-04 19:28 Desc 文件描述
 */
@RestController
@Slf4j
public class TestCtrl {
    
    @Resource
    private TestService testService;
    
    @RequestMapping("hello")
    @ResponseBody
    public String hello(HttpSession httpSession){
        log.info("sessionId {}", httpSession.getId());
        httpSession.setAttribute("user", "mingle");
        return "hello " + testService.getTime();
    }
    
    @RequestMapping("world")
    @ResponseBody
    public String world(HttpSession httpSession){
        log.info("sessionId {}", httpSession.getId());
        String user = (String) httpSession.getAttribute("user");
        return "hello " + user;
    }
}
