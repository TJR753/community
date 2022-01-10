package com.example.community.controller;

import com.example.community.domain.DiscussPost;
import com.example.community.domain.vo.Page;
import com.example.community.service.CommentService;
import com.example.community.service.ElasticsearchService;
import com.example.community.service.LikeService;
import com.example.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController {
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Value("${community.discusspost.indexname}")
    private String indexName;

    @RequestMapping(path = "/search",method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model){
        page.setLimit(10);

        List<DiscussPost> postList = null;
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        try {
             postList = elasticsearchService.searchDiscussPost(keyword, page.getCurrent(),page.getLimit(),indexName);
             if(postList!=null){
                 for(DiscussPost dp:postList){
                     HashMap<String, Object> map = new HashMap<>();
                     map.put("post",dp);
                     map.put("user",userService.getUserById(dp.getUserId()));
                     map.put("like",likeService.likeCount(dp.getType(),dp.getId()));
                     map.put("reply",commentService.findCommentCount(dp.getType(),dp.getId()));
                     list.add(map);
                 }
             }
        } catch (IOException e) {
            e.printStackTrace();
        }
        model.addAttribute("posts",list);
        model.addAttribute("keyword",keyword);

        page.setRows(postList.size());
        page.setPath("/search"+keyword);
        return "/site/search";
    }
}
