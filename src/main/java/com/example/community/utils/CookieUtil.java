package com.example.community.utils;


import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieUtil {
    //获取ticket值
    public static String getTicket(HttpServletRequest request,String name){
        //判空
        if(request==null||StringUtils.isBlank(name)){
            return null;
        }
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for(Cookie cookie:cookies){
                if(cookie.getName().equals(name)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
    //移除cookie
    public static void removeTicket(HttpServletRequest request,String name){
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for(Cookie cookie:cookies){
                if(cookie.getName().equals(name)){
                    cookie.setMaxAge(0);
                }
            }
        }
    }
}
