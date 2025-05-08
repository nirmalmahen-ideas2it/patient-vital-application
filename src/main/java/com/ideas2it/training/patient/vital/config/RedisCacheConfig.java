package com.ideas2it.training.patient.vital.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Configuration for Redis caching in the application.
 *
 * <p>This class sets up the Redis connection factory, Redis template, and cache manager
 * with custom serialization settings. It uses Lettuce as the Redis client and configures
 * Jackson for JSON serialization, including support for Java 8 date and time types.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * RedisTemplate<String, Object> template = new RedisCacheConfig().redisTemplate(factory);
 * </pre>
 *
 * <p>Note: Ensure that the Redis server is running and the required properties are
 * configured in the application properties file.</p>
 *
 * @author Alagu Nirmal Mahendran
 * @version 1.0
 * @since 06/05/2025
 */
@Configuration
public class RedisCacheConfig {

    @Value("${spring.cache.redis.host}")
    private String redisHost;

    @Value("${spring.cache.redis.port}")
    private int redisPort;

    @Value("${spring.cache.redis.password}")
    private String redisPassword;

    /**
     * Configures the Redis connection factory.
     *
     * <p>This method sets up a standalone Redis configuration with the host, port,
     * and password specified in the application properties file.</p>
     *
     * @return the configured {@link RedisConnectionFactory}
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
        config.setPassword(RedisPassword.of(redisPassword));
        return new LettuceConnectionFactory(config);
    }

    /**
     * Configures the Redis template for interacting with Redis.
     *
     * <p>This method sets up the key and value serializers for the Redis template,
     * using Jackson for JSON serialization and deserialization.</p>
     *
     * @param factory the Redis connection factory
     * @return the configured {@link RedisTemplate}
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());

        // Configure Jackson JSON serializer
        Jackson2JsonRedisSerializer<Object> jsonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.activateDefaultTyping(
            mapper.getPolymorphicTypeValidator(),
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        );
        jsonSerializer.setObjectMapper(mapper);
        template.setValueSerializer(jsonSerializer);
        return template;
    }
}
