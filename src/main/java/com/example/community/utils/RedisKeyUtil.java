package com.example.community.utils;

public class RedisKeyUtil {
    private static final String SPLIT=":";
    private static final String PREFIX_ENTITY_LIKE="like:entity";
    private static final String PREFIX_USER_LIKE="like:user";
    private static final String PREFIX_USER_FOLLOWEE="followee";
    private static final String PREFIX_USER_FOLLOWER="follower";
    private static final String PREFIX_KAPTCHA="kaptcha";
    private static final String PREFIX_TICKET="ticket";
    private static final String PREFIX_USER="user";
    private static final String PREFIX_UV="uv";
    private static final String PREFIX_DAU="dau";
    private static final String PREFIX_POST_SCORE="post";

    //某个实体的赞
    //like:entity:entityType:entityId->set(user)
    public static String getEntityLikeKey(int entityType,int entityId){
        return PREFIX_ENTITY_LIKE+SPLIT+entityType+SPLIT+entityId;
    }
    //某个用户的赞
    //like:user:userId-->int
    public static String getUserEntityKey(int userId){
        return PREFIX_USER_LIKE+SPLIT+userId;
    }
    //某个用户关注的实体
    //followee:userId:entityType-->zset(entityId,now)
    public static String getFolloweeEntityKey(int userId,int entityTpye){
        return PREFIX_USER_FOLLOWEE+SPLIT+userId+SPLIT+entityTpye;
    }
    //某个实体的粉丝
    //follower:entityType:entityId-->zset(userId,now)
    public static String getFollowerEntityKey(int entityType,int entityId){
        return PREFIX_USER_FOLLOWER+SPLIT+entityType+SPLIT+entityId;
    }
    //验证码
    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA+SPLIT+owner;
    }
    /**
     * 用户登录凭证
     */
    public static String getTicketKey(String ticket){
        return PREFIX_TICKET+SPLIT+ticket;
    }
    //缓存用户信息
    public static String getUserKey(int userId){
        return PREFIX_USER+SPLIT+userId;
    }

    /**
     * 独立访客主键
     * @param ip 用户ip
     * @return UV的redisKey
     */
    public static String getUVKey(String ip){
        return PREFIX_UV+SPLIT+ip;
    }
    public static String getUVKey(String start,String end){
        return PREFIX_UV+SPLIT+start+SPLIT+end;
    }

    /**
     * 日活跃主键
     * @param userId 用户id
     * @return DAU的redisKey
     */
    public static String getDAUKey(String userId){
        return PREFIX_UV+SPLIT+userId;
    }
    public static String getDAUKey(String start,String end){
        return PREFIX_UV+SPLIT+start+SPLIT+end;
    }
    /**
     * 分数主键
     */
    public static String getPostScoreKey(){
        return PREFIX_POST_SCORE+SPLIT+"score";
    }
}
