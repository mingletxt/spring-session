package com.mingle.spring.session.http;

import org.springframework.stereotype.Service;

import java.util.Date;


/**
 * Created by mingle. Time 2019-06-04 19:43 Desc 文件描述
 */
@Service
public class TestService {
    
    public Date getTime() {
        return new Date();
    }
}
