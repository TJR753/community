package com.example.community.utils;

import com.example.community.domain.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtil {
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    public static String md5(String password){
        if (StringUtils.isBlank(password)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(password.getBytes());
    }
}
