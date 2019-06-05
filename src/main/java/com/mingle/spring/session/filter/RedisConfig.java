package com.mingle.spring.session.filter;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.extern.slf4j.Slf4j;


/**
 * Created by mingle. Time 2019-06-05 17:08 Desc 文件描述
 */
@Configuration
@Slf4j
public class RedisConfig extends CachingConfigurerSupport {
    
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate redisTemplate=new RedisTemplate();
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer());
        log.info("config redis suscessful");
        return redisTemplate;
    }
}
