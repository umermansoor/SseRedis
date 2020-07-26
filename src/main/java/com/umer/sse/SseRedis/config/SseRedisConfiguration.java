package com.umer.sse.SseRedis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.concurrent.Executors;

@Configuration
@ComponentScan("com.umer.sse.SseRedis")
public class SseRedisConfiguration {

    @Value("${redis.container.threads}")
    private int redisContainerThreads;


    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    RedisMessageListenerContainer redisContainer() {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory());
        container.setTaskExecutor(Executors.newFixedThreadPool(redisContainerThreads));
        return container;
    }






}
