package com.mingle.spring.session.http;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;


/**
 * Created by mingle. Time 2019-06-04 19:28 Desc 文件描述
 */
@RestController
public class TestCtrl {
    
    @Resource
    private TestService testService;
    
    @RequestMapping("hello")
    @ResponseBody
    public String hello(){
        return "hello " + testService.getTime();
    }
    
}
