package com.example.community.controller;

import com.example.community.domain.Event;
import com.example.community.domain.User;
import com.example.community.event.EventProducer;
import com.example.community.service.LikeService;
import com.example.community.utils.CommunityConstant;
import com.example.community.utils.CommunityUtil;
import com.example.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

@Controller
public class LikeController implements CommunityConstant {
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/like",method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType,int entityId,int entityUserId,int postId){
        User user = hostHolder.getUser();
        likeService.like(user.getId(), entityType,entityId,entityUserId);
        long likeCount = likeService.likeCount(entityType, entityId);
        int likeStatus = likeService.likeStatus(user.getId(), entityType, entityId);
        HashMap<String, Object> map = new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);
        //触发点赞事件
        if(likeStatus==1){
            Event event = new Event();
            event.setTopic(TOPIC_LIKE)
                    .setUserId(user.getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId",postId);
            eventProducer.fireEvent(event);
        }
        return CommunityUtil.parseJson("0",null,map);
    }
}
