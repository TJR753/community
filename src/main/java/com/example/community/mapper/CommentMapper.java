package com.example.community.mapper;

import com.example.community.domain.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentMapper {
    List<Comment> findCommentsByEntity(int entityType, String entityId, int offset, int limit);

    int findCommentCount(int entityType, int entityId);

    int add(Comment comment);
    //查找我的回复，entityType为1
    List<Comment> getMyReply(int userId);
}
