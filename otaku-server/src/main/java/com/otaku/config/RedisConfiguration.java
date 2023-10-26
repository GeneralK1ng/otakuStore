package com.otaku.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;


// 使用@Configuration注解表示这是一个Spring配置类，它包含了Spring容器的配置信息。
@Configuration
@Slf4j
public class RedisConfiguration {


    // 使用@Bean注解将这个方法的返回值注册为一个Spring Bean，以供应用程序其他部分使用。
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {

        // 通过@Slf4j注解引入日志记录功能，用于在应用程序中输出日志信息。
        log.info("开始创建redis对象...");

        // 创建一个RedisTemplate对象，用于与Redis数据库进行交互。
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // 设置RedisTemplate的连接工厂，以便与Redis数据库建立连接。
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 配置RedisTemplate的键（Key）的序列化器为StringRedisSerializer，以将键序列化为字符串。
        redisTemplate.setKeySerializer(new StringRedisSerializer());


        // 在这里通常还需要配置值（Value）的序列化器，以便将对象序列化为存储在Redis中的格式。

        // 返回配置好的RedisTemplate Bean，以便在应用程序中使用它与Redis数据库交互。
        return redisTemplate;

    }
}