package com.example.community.controller;

import com.example.community.domain.Comment;
import com.example.community.domain.DiscussPost;
import com.example.community.domain.Event;
import com.example.community.domain.User;
import com.example.community.event.EventProducer;
import com.example.community.mapper.CommentMapper;
import com.example.community.service.CommentService;
import com.example.community.service.DiscussPostService;
import com.example.community.utils.CommunityConstant;
import com.example.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private CommentService commentService;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private EventProducer eventProducer;


    @RequestMapping(path = "/add/{discussPostId}",method = RequestMethod.POST)
    public String add(@PathVariable("discussPostId")String discussPostId, Comment comment){
        User user = hostHolder.getUser();
        comment.setUserId(user.getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.add(comment);
        /**
         * 如果是post，则需要更新帖子数量
         */
        DiscussPost discussPost=null;
        if(comment.getEntityType()==ENTITY_TYPE_POST){
            discussPost = discussPostService.findDiscussPostById(discussPostId);
            commentService.updateCommentCount(discussPost.getCommentCount(),discussPostId);
        }
        /**
         * 触发评论事件，推送系统通知
         */
        Event event = new Event();
        event.setTopic(TOPIC_COMMENT)
                .setUserId(user.getId())
                .setEntityId(comment.getEntityId())
                .setEntityType(comment.getEntityType())
                .setData("postId",discussPostId);
        //获得作者id
        if(comment.getEntityType()==ENTITY_TYPE_POST){
            event.setEntityUserId(discussPost.getUserId());
        }else{
            //评论是对某个人的评论
            Comment comment1=commentService.findComment(comment.getEntityId());
            event.setEntityUserId(comment1.getUserId());
        }
        eventProducer.fireEvent(event);
        return "redirect:/getDetail/"+discussPostId;
    }
}
