package com.haizhi.authcenter;

import com.haizhi.authcenter.config.SpringConfig;
import com.haizhi.authcenter.constants.Permission;
import com.haizhi.authcenter.constants.RoleType;
import com.haizhi.authcenter.util.Utils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.*;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.Factory;
import org.apache.shiro.util.SimpleByteSource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.security.Key;
import java.util.concurrent.TimeUnit;


/**
 * Created by haizhi on 2017/10/9.

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        SpringConfig.class
}) */
public class TestStartup {

    @Test
    public void testLogin(){
        Subject subject = login("aaa", "a24736008fd69b0e33558ab24717b348421e9a6a304e8071b733f8c56709159b");

        Assert.assertTrue(subject.isAuthenticated()); //断言用户已经登录

        //6、退出
        subject.logout();
    }

    private Subject login(String username,String password){
        //1、获取SecurityManager工厂，此处使用Ini配置文件初始化SecurityManager
        Factory<SecurityManager> factory =
                new IniSecurityManagerFactory("classpath:shiro.ini");
        //2、得到SecurityManager实例 并绑定给SecurityUtils
        org.apache.shiro.mgt.SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);
        //3、得到Subject及创建用户名/密码身份验证Token（即用户身份/凭证）
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username,password);

        try {
            //4、登录，即身份验证
            subject.login(token);
        } catch (AuthenticationException e) {
            //5、身份验证失败
        }
        return subject;
    }

    @Test
    public void testRole(){
        Subject user = login("admin", "123");

        Assert.assertTrue(user.hasRole(RoleType.GUIDE));

        Assert.assertTrue(user.isPermitted(Permission.UPDATE));
    }

    @Test
    public void testBase64(){
        String str = "hello";
        String base64Encoded = Base64.encodeToString(str.getBytes());
        String str2 = Base64.decodeToString(base64Encoded);
        Assert.assertEquals(str, str2);
    }

    @Test
    public void testHex(){
        String str = "hello";
        String hexStr = Hex.encodeToString(str.getBytes());
        String str2 = new String(Hex.decode(hexStr.getBytes()));
        Assert.assertEquals(str, str2);
    }

    @Test
    public void testHash(){
        String str = "bbb";
        String salt = "123456";
        String md5Str = new Md5Hash(str, salt).toString();//还可以转换为 toBase64()/toHex()

        String sha1 = new Sha1Hash(str, salt).toString();
        String sha256 = new Sha256Hash(str, salt).toString();
        String sha512 = new Sha512Hash(str, salt).toString();
        String simpleHash = new SimpleHash("SHA-1", str, salt).toString();

        System.out.println("md5        : "+md5Str);

        System.out.println("sha1       : "+sha1);
        System.out.println("sha256     : "+sha256);
        System.out.println("sha512     : "+sha512);

        System.out.println("simpleHash : "+simpleHash);
    }

    @Test
    public void testDefaultHash(){
        DefaultHashService hashService = new DefaultHashService(); //默认算法SHA-512
        hashService.setHashAlgorithmName("SHA-512");
        hashService.setPrivateSalt(new SimpleByteSource("123")); //私盐，默认无
        hashService.setGeneratePublicSalt(true);//是否生成公盐，默认false
        SecureRandomNumberGenerator salt = new SecureRandomNumberGenerator();

        System.out.println("salt : "+salt.getSecureRandom().getAlgorithm());

        hashService.setRandomNumberGenerator(salt);//用于生成公盐。默认就这个
        hashService.setHashIterations(1); //生成Hash值的迭代次数

        HashRequest request = new HashRequest.Builder()
                .setAlgorithmName("MD5").setSource(ByteSource.Util.bytes("hello"))
                .setSalt(ByteSource.Util.bytes("123")).setIterations(2).build();
        String hex = hashService.computeHash(request).toHex();

        System.out.println(hex);
    }

    @Test
    public void testAES(){
        AesCipherService aesCipherService = new AesCipherService();
        aesCipherService.setKeySize(128); //设置key长度
        //生成key
        Key key = aesCipherService.generateNewKey();

        String text = "bbb";


        //加密
        String encrptText = aesCipherService.encrypt(text.getBytes(), key.getEncoded()).toHex();

        //解密
        String text2 = new String(aesCipherService.decrypt(Hex.decode(encrptText), key.getEncoded()).getBytes());

        Assert.assertEquals(text, text2);

        System.out.println("key : "+ Utils.bytesToHexString(key.getEncoded()));
        System.out.println("密文 : "+encrptText);
        System.out.println("明文 : "+text2);
    }

    @Test
    public void testPwd(){
        AesCipherService aesCipherService = new AesCipherService();
        aesCipherService.setKeySize(128); //设置key长度

        String text = "bbb";

        //加密
        String encrptText = aesCipherService.encrypt(text.getBytes(),
                Utils.hexStringToBytes(com.haizhi.authcenter.constants.Key.AES)).toHex();

        System.out.println("密文 : "+encrptText);
        //a720e01a52babc386cc588766cd914c953d1804a010610acef4af82e9096cece
    }

    @Test
    public void testToken() {
        String token = Utils.generateToken(2L);
        System.out.println(token);

        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //System.out.println(Utils.getUserID(token));
        try {
            Utils.validateToken(token);
        } catch (Exception e) {
            System.err.println("token 过期");
        }

    }
}
