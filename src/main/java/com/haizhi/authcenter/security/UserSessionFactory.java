package com.haizhi.authcenter.security;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionFactory;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.web.session.mgt.WebSessionContext;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by JuniFire on 2017/10/19.
 */
public class UserSessionFactory implements SessionFactory {

    @Override
    public Session createSession(SessionContext initData) {
        SimpleSession session = new SimpleSession();

        if (initData != null && initData instanceof WebSessionContext) {
            WebSessionContext sessionContext = (WebSessionContext) initData;
            HttpServletRequest request = (HttpServletRequest) sessionContext.getServletRequest();
            if (request != null) {
                session.setHost(request.getLocalAddr());
                session.setAttribute("userAgent",request.getHeader("User-Agent"));
                session.setAttribute("host",request.getLocalAddr() + ":" + request.getLocalPort());
            }
        }

        return session;
    }
}
