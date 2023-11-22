package com.hiraeth.im.cache.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Description
 * Created by leo on 2021/1/13 11:31
 */
@Configuration
@ComponentScan(basePackages = "com.hiraeth.im.cache")
// springboot 已经集成 RedisProperties, 故无需自行实现 RedisProperties
// RedisProperties 该类位于 org.springframework.boot.autoconfigure.data.redis
@Import(RedisConfiguration.class)
public class RedisAutoConfiguration {
}
