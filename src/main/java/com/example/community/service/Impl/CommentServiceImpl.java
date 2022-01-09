package com.example.community.service.Impl;

import com.example.community.domain.Comment;
import com.example.community.mapper.CommentMapper;
import com.example.community.mapper.DiscussPostMapper;
import com.example.community.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Override
    public List<Comment> findCommentsByEntity(int entityType, String entityId, int offset, int limit) {
        List<Comment> commentList=commentMapper.findCommentsByEntity(entityType,entityId,offset,limit);
        return commentList;
    }

    @Override
    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.findCommentCount(entityType,entityId);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public int add(Comment comment) {
        return commentMapper.add(comment);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public int updateCommentCount(Integer commentCount, String discussPostId) {
        return discussPostMapper.updateCommentCount(commentCount,discussPostId);
    }

    @Override
    public List<Comment> getMyReply(int userId) {
        return commentMapper.getMyReply(userId);
    }

    @Override
    public Comment findComment(int entityId) {
        return commentMapper.findComment(entityId);
    }
}
