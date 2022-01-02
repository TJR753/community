package com.example.community.mapper;

import com.example.community.domain.DiscussPost;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscussPostMapper {
    List<DiscussPost> findDiscussPosts(int userId, int offset, int limit);

    int findDiscussRows(int userId);
}
