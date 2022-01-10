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
    /**
     * 实体类型：帖子
     */
    int ENTITY_TYPE_POST=1;
    /**
     * 实体类型：评论
     */
    int ENTITY_TYPE_COMMENT=2;
    /**
     * 实体类型: 人
     */
    int ENTITY_TYPE_USER=3;
    /**
     * 消息主题：关注
     */
    String TOPIC_FOLLOW="follow";
    /**
     * 消息主题：评论
     */
    String TOPIC_COMMENT="comment";
    /**
     * 消息主题：点赞
     */
    String TOPIC_LIKE="like";
    /**
     * 消息主题：发布
     */
    String TOPIC_PUBLISH="publish";
    /**
     * 系统用户id
     */
    int SYSTEM_USER_ID=1;
}
