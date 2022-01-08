package com.example.community.service;

import com.example.community.domain.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> findCommentsByEntity(int entityType, String entityId, int offset, int limit);

    int findCommentCount(int entityType, int entityId);

    int add(Comment comment);

    int updateCommentCount(Integer commentCount, String discussPostId);

    List<Comment> getMyReply(int userId);
}
