package com.wenxiahy.hy.redis.config;

import com.wenxiahy.hy.redis.service.RedisService;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

/**
 * @Author zhouw
 * @Description
 * @Date 2020-12-18
 */
@Configuration
public class OrderRedisConfig extends AbstractRedisConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderRedisConfig.class);

    @Value("${spring.redis.order.host:localhost}")
    private String host;

    @Value("${spring.redis.order.port:6379}")
    private int port;

    @Value("${spring.redis.order.password:animal}")
    private String password;

    @Value("${spring.redis.order.timeout:30000}")
    private int timeout;

    @Bean("orderRedisFactory")
    public RedisConnectionFactory connectionFactory(GenericObjectPoolConfig poolConfig) {
        // standalone模式
        RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration(host, port);
        standaloneConfiguration.setPassword(password);
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(timeout))
                .poolConfig(poolConfig)
                .build();

        return new LettuceConnectionFactory(standaloneConfiguration, clientConfiguration);
    }

    @Bean("orderRedisTemplate")
    public RedisTemplate<String, Object> createRedisTemplate(@Qualifier("orderRedisFactory") RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        configureRedisTemplate(redisTemplate);
        return redisTemplate;
    }

    @Bean("orderRedisService")
    public RedisService createRedisService(@Qualifier("orderRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        LOGGER.info("orderRedisService配置成功，{}:{}", host, port);
        return new RedisService(redisTemplate);
    }
}
