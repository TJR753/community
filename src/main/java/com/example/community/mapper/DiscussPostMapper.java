package com.example.community.mapper;

import com.example.community.domain.DiscussPost;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscussPostMapper {
    List<DiscussPost> findDiscussPosts(int userId, int offset, int limit);

    int findDiscussRows(int userId);

    int addDiscussPost(DiscussPost discussPost);

    DiscussPost findDiscussPostById(String id);

    int updateCommentCount(Integer commentCount, String discussPostId);
    //查找我的贴子，拉黑的不找
    List<DiscussPost> getMyPost(int userId);

    int updateType(int id);
    int updateStatus(int id,int status);
}
