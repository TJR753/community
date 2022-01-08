package com.example.community.service;

import java.util.List;
import java.util.Map;

public interface FollowService {
    //关注
    void follow(int userId,int entityType,int entityId);
    //取消关注
    void unfollow(int userId,int entityType,int entityId);
    //关注了几人
     long followeeCount(int userId,int entityType);
    //关注者,即被关注
    long followerCount(int entityId,int entityType);
    //当前用户是否关注该实体
    boolean hasFollowed(int userId,int entityType,int entityId);
    //查询某个用户的关注列表
    List<Map<String,Object>> findFollowee(int userId,int offset,int limit);
    //查询某个用户的粉丝列表
    List<Map<String,Object>> findFollower(int userId,int offset,int limit);
}
