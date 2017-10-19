package com.haizhi.authcenter.security;

import com.haizhi.authcenter.cache.impl.CacheToken;
import com.haizhi.authcenter.util.SerializableUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Created by JuniFire on 2017/10/19.
 */
@Component
public class UserSessionDao extends CachingSessionDAO {

    @Autowired
    private CacheToken cacheToken;

    @Override
    protected void doUpdate(Session session) {
        if(session instanceof ValidatingSession && !((ValidatingSession)session).isValid()) {
            return; //如果会话过期/停止 没必要再更新了
        }
    }

    @Override
    protected void doDelete(Session session) {

    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        this.cacheToken.set(sessionId.toString(), SerializableUtils.serialize(session));
        return session.getId();
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        return SerializableUtils.deserialize(this.cacheToken.get(sessionId.toString()));
    }

    public void setCacheToken(CacheToken cacheToken) {
        this.cacheToken = cacheToken;
    }
}
