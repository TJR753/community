package com.example.community.service;

public interface LikeService {
    //点赞功能
    void like(int userId,int entityType,int entityId,int entityUserId);
    //点赞数量
    long likeCount(int entityType,int entityId);
    //点赞状态
    int likeStatus(int userId,int entityType,int entityId);
    //查询某个用户的点赞数量
    int userLikeCount(int userId);
}
