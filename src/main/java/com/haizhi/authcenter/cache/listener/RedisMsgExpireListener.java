package com.haizhi.authcenter.cache.listener;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import redis.clients.jedis.JedisPubSub;

/**
 * Created by JuniFire on 2017/10/22.
 */
public class RedisMsgExpireListener implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        System.out.println("Message received: " + message.toString() );
    }

}
