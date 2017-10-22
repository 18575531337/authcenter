package com.haizhi.authcenter.cache.impl;

import com.haizhi.authcenter.cache.Cache;
import com.haizhi.authcenter.cache.CallBackListener;
import com.haizhi.authcenter.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.Calendar;

/**
 * Created by JuniFire on 2017/10/18.
 */
@Component("cacheCommon")
public class CacheCommon implements Cache<String,String> {

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    @Override
    public void set(String key, String value,Long expireTime) {
        this.redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                if(expireTime != null) {
                    connection.set(key.getBytes(),value.getBytes(),Expiration.milliseconds(expireTime),
                            RedisStringCommands.SetOption.UPSERT);
                } else {
                    connection.set(key.getBytes(),value.getBytes());
                }

                return null;
            }
        });
    }

    @Override
    public String get(String key) {

        return this.redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                String v = null;
                try {
                    v = new String(connection.get(key.getBytes()), "UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return v;
            }
        });
    }

    @Override
    public void del(String key) {
        this.redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                connection.del(key.getBytes());
                return null;
            }
        });
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
