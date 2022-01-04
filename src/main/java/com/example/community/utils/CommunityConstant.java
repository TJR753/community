package com.example.community.utils;

public interface CommunityConstant {
    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS=0;
    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT=1;
    /**
     * 激活失败
     */
    int ACTIVATION_FAIL=2;
    /**
     * 默认存活时间
     */
    int EXPIRED_SECONDS=3600*12;
    /**
     * rememberMe 存活时间
     */
    int REMEMBER_EXPIRED_SECONDS=3600*12*100;
}
