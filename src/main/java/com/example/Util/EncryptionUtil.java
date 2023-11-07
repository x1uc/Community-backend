package com.example.Util;


import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.crypto.digest.MD5;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class EncryptionUtil {
    private static String SALT = "conversion";

    public static String passwordEncryption(String password) {
        password = DigestUtil.md5Hex(password);
        password = DigestUtil.md5Hex(password + SALT);
        return password;
    }


}
