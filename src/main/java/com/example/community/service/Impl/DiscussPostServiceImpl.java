package com.example.community.service.Impl;

import com.example.community.domain.DiscussPost;
import com.example.community.mapper.DiscussPostMapper;
import com.example.community.service.DiscussPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostServiceImpl implements DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Override
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        List<DiscussPost> discussPostList=discussPostMapper.findDiscussPosts(userId,offset,limit);
        return discussPostList;
    }

    @Override
    public int findDiscussPostRows(int userId) {
        int total=discussPostMapper.findDiscussRows(userId);
        return total;
    }
}
