package com.haizhi.authcenter.security;

import com.haizhi.authcenter.cache.impl.CacheCommon;
import com.haizhi.authcenter.util.SerializableUtils;
import com.haizhi.authcenter.util.Utils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by JuniFire on 2017/10/19.
 */
@Component
public class UserSessionDao extends CachingSessionDAO {

    @Autowired
    private CacheCommon cacheCommon;

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
        this.cacheCommon.set(sessionId.toString(), SerializableUtils.serialize(session),
                Utils.getExpireDate(12, Calendar.HOUR).getTimeInMillis());
        return session.getId();
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        return SerializableUtils.deserialize(this.cacheCommon.get(sessionId.toString()));
    }

    public void setCacheToken(CacheCommon cacheToken) {
        this.cacheCommon = cacheToken;
    }
}
