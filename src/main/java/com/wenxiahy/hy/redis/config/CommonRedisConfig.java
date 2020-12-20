package com.wenxiahy.hy.redis.config;

import com.wenxiahy.hy.redis.service.RedisService;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
public class CommonRedisConfig extends AbstractRedisConfig {

    @Value("${spring.redis.common.host:localhost}")
    private String host;

    @Value("${spring.redis.common.port:6379}")
    private int port;

    @Value("${spring.redis.common.password:animal}")
    private String password;

    @Value("${spring.redis.common.timeout:30000}")
    private int timeout;

    @Bean("commonRedisFactory")
    @Primary
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

    @Bean("commonRedisTemplate")
    public RedisTemplate<String, Object> createRedisTemplate(@Qualifier("commonRedisFactory") RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        configureRedisTemplate(redisTemplate);
        return redisTemplate;
    }

    @Bean("commonRedisService")
    public RedisService createRedisService(@Qualifier("commonRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        System.out.println(host + ":" + port + "，commonRedisService配置成功");
        return new RedisService(redisTemplate);
    }
}
