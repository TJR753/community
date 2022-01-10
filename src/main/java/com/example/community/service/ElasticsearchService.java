package com.example.community.service;

import com.example.community.domain.DiscussPost;

import java.io.IOException;
import java.util.List;

public interface ElasticsearchService {
    //增加
    void saveDiscussPost(DiscussPost discussPost);
    //删除
    void deleteDiscussPost(DiscussPost discussPost);
    //搜索
    List<DiscussPost> searchDiscussPost(String keywords,int current,int limit,String index) throws IOException;
}
