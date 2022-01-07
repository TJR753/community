package com.example.community.utils;

import com.alibaba.fastjson.JSONObject;
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

    public static String parseJson(String code,String msg){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        return json.toString();
    }
    public static String parseJson(String code,String msg,Object o){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        json.put("map",o);
        return json.toString();
    }
}
