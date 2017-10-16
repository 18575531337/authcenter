package com.haizhi.authcenter.security;

import com.haizhi.authcenter.constants.Key;
import com.haizhi.authcenter.util.Utils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.stereotype.Component;

/**
 * Created by haizhi on 2017/10/13.
 * 如在1个小时内密码最多重试5次，如果尝试次数超过5次就锁定1小时，1小时后可再次重试，如果还是重试失败，
 * 可以锁定如1天，以此类推，防止密码被暴力破解。
 */
@Component
public class UserCredentialMatcher extends HashedCredentialsMatcher {

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        String username = (String)token.getPrincipal();

        String cipherPassword = token.getCredentials().toString();

        AesCipherService aesCipherService = new AesCipherService();
        aesCipherService.setKeySize(128); //设置key长度
        String password = new String(aesCipherService.decrypt(Hex.decode(cipherPassword),
                Utils.hexStringToBytes(Key.AES)).getBytes());

        String pwdHashStr = new SimpleHash("SHA-512", password, Key.SALT).toString();

        //retry count + 1
        /*
        Element element = passwordRetryCache.get(username);
        if(element == null) {
            element = new Element(username , new AtomicInteger(0));
            passwordRetryCache.put(element);
        }
        AtomicInteger retryCount = (AtomicInteger)element.getObjectValue();
        if(retryCount.incrementAndGet() > 5) {
            //if retry count > 5 throw
            throw new ExcessiveAttemptsException();
        }
*/

        //boolean isMatch = super.doCredentialsMatch(token, info);
        if(info.getCredentials().equals(pwdHashStr)) {
            return true;
        }
        return false;
    }
}
