package com.haizhi.authcenter.config;

import com.haizhi.authcenter.cache.listener.RedisMsgExpireListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by JuniFire on 2017/10/17.
 */
@Configuration
public class SpringConfigRedis {

    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private int port;

    @Bean
    public JedisConnectionFactory getRedisConnFactory() {
        JedisConnectionFactory redisConneFactory = new JedisConnectionFactory();

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(60000);
        jedisPoolConfig.setMaxWaitMillis(2000);
        jedisPoolConfig.setTestOnBorrow(true);

        redisConneFactory.setPoolConfig(jedisPoolConfig);
        redisConneFactory.setUsePool(true);
        redisConneFactory.setHostName(this.getHost());
        redisConneFactory.setPort(this.getPort());

        return redisConneFactory;
    }

    @Bean
    public RedisTemplate<String, String> getRedisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(getRedisConnFactory());
        //redisTemplate.setEnableTransactionSupport(false);
        //redisTemplate.setExposeConnection(false);
        return redisTemplate;
    }

/*
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer() {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(getRedisConnFactory());

        redisMessageListenerContainer.addMessageListener(new RedisMsgExpireListener(),
                new ChannelTopic("user-status"));

        return redisMessageListenerContainer;
    }
*/

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
