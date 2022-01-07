package com.example.community.controller;

import com.example.community.domain.Comment;
import com.example.community.domain.DiscussPost;
import com.example.community.domain.User;
import com.example.community.mapper.CommentMapper;
import com.example.community.service.CommentService;
import com.example.community.service.DiscussPostService;
import com.example.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private CommentService commentService;
    @Autowired
    private DiscussPostService discussPostService;
    @RequestMapping(path = "/add/{discussPostId}",method = RequestMethod.POST)
    public String add(@PathVariable("discussPostId")String discussPostId, Comment comment){
        User user = hostHolder.getUser();
        comment.setUserId(user.getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.add(comment);
        if(comment.getEntityType()==1){
            DiscussPost discussPost = discussPostService.findDiscussPostById(discussPostId);
            commentService.updateCommentCount(discussPost.getCommentCount(),discussPostId);
        }
        return "redirect:/getDetail/"+discussPostId;
    }
}
