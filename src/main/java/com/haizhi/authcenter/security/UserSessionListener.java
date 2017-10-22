package com.haizhi.authcenter.security;

import com.haizhi.authcenter.cache.impl.CacheCommon;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by JuniFire on 2017/10/19.
 */
@Component("userSessionListener")
public class UserSessionListener implements SessionListener {

    @Autowired
    private CacheCommon cacheToken;

    @Override
    public void onStart(Session session) {

    }

    @Override
    public void onStop(Session session) {
        this.cacheToken.del("user_session_"+session.getAttribute("userID"));
        session.stop();

    }

    @Override
    public void onExpiration(Session session) {
        this.cacheToken.del("user_session_"+session.getAttribute("userID"));
        session.stop();
    }

    public void setCacheToken(CacheCommon cacheToken) {
        this.cacheToken = cacheToken;
    }
}
