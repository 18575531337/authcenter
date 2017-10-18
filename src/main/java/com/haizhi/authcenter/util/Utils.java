package com.haizhi.authcenter.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import com.haizhi.authcenter.constants.Key;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by haizhi on 2017/10/16.
 */
public class Utils {

    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
    /**
     * Convert hex string to byte[]
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    /**
     * Convert char to byte
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    private static Algorithm getTokenAlgorithm() {
        Algorithm algorithm = null;
        try {
            algorithm = Algorithm.HMAC256(Key.TOKEN);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return algorithm;
    }

    public static String generateToken(Long userID) {

        Calendar expireDate = Calendar.getInstance();
        //半天后过期
        expireDate.set(Calendar.HOUR,expireDate.get(Calendar.HOUR)+12);
        /**
         * for test
        expireDate.set(Calendar.SECOND,expireDate.get(Calendar.SECOND)+3);
         */
        String token = JWT.create()
                .withExpiresAt(expireDate.getTime())
                .withClaim("userID",userID)
                .sign(getTokenAlgorithm());
        return token;
    }

    public static void validateToken(String token) {
        Verification verification = JWT.require(getTokenAlgorithm());
        verification.build().verify(token);
    }

    public static Long getUserID(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getClaim("userID").as(Long.TYPE);
    }

    public static Calendar getExpireDate(int value,int calendarType){
        Calendar expireDate = Calendar.getInstance();
        expireDate.set(calendarType,expireDate.get(calendarType)+value);
        return expireDate;
    }
}
