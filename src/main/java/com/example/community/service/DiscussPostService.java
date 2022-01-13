package com.example.community.service;

import com.example.community.domain.DiscussPost;

import java.util.List;

public interface DiscussPostService {
    List<DiscussPost> findDiscussPosts(int userId, int offset, int limit);

    int findDiscussPostRows(int userId);

    String addDiscussPost(String title, String content);

    DiscussPost findDiscussPostById(String id);

    List<DiscussPost> getMyPost(int userId);

    int updateType(int id);

    int updateStatus(int id,int status);
}
