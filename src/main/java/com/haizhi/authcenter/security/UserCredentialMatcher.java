package com.haizhi.authcenter.security;

import com.haizhi.authcenter.cache.Cache;
import com.haizhi.authcenter.constants.Key;
import com.haizhi.authcenter.constants.RedisKey;
import com.haizhi.authcenter.constants.UserStatus;
import com.haizhi.authcenter.util.Utils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;

/**
 * Created by haizhi on 2017/10/13.
 * 如在1个小时内密码最多重试5次，如果尝试次数超过5次就锁定1小时，1小时后可再次重试，如果还是重试失败，
 * 可以锁定如1天，以此类推，防止密码被暴力破解。
 */
@Component
public class UserCredentialMatcher implements CredentialsMatcher {

    @Resource(name = "cacheCommon")
    Cache<String,String> cacheCommon;

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        String cipherPassword = String.copyValueOf((char[])token.getCredentials());

        AesCipherService aesCipherService = new AesCipherService();
        aesCipherService.setKeySize(128); //设置key长度
        /**/
        String password = new String(aesCipherService.decrypt(Hex.decode(cipherPassword),
                Utils.hexStringToBytes(Key.AES)).getBytes());

        String pwdHashStr = new SimpleHash("SHA-512", password, Key.SALT).toString();

        if (info.getCredentials().equals(pwdHashStr)) {
            return true;
        }

        //密码重试5次 防止暴力破解
        String keyRetry = RedisKey.USER_RETRY+token.getPrincipal().toString();
        String retryStatus = this.cacheCommon.get(keyRetry);
        if (retryStatus == null || Integer.valueOf(retryStatus) < 5) {
            this.cacheCommon.incAtomic(keyRetry, Utils.getExpireDate(1, Calendar.HOUR).getTimeInMillis());
        } else {
            this.cacheCommon.set(keyRetry, UserStatus.LOCKED,
                    Utils.getExpireDate(1, Calendar.HOUR).getTimeInMillis());
        }

        return false;
    }

    public void setCacheCommon(Cache<String, String> cacheCommon) {
        this.cacheCommon = cacheCommon;
    }
}
