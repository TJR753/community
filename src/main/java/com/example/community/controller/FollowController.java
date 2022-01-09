package com.example.community.controller;

import com.example.community.domain.Event;
import com.example.community.domain.User;
import com.example.community.domain.vo.Page;
import com.example.community.event.EventProducer;
import com.example.community.service.FollowService;
import com.example.community.service.UserService;
import com.example.community.utils.CommunityConstant;
import com.example.community.utils.CommunityUtil;
import com.example.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConstant {
    @Autowired
    private FollowService followService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/follow",method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId){
        User user = hostHolder.getUser();
        followService.follow(user.getId(), entityType,entityId);
        //触发关注事件
        Event event = new Event();
        event.setTopic(TOPIC_FOLLOW)
                .setUserId(user.getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);
        return CommunityUtil.parseJson("0", "已关注");
    }
    @RequestMapping(path = "/unfollow",method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType, int entityId){
        User user = hostHolder.getUser();
        followService.unfollow(user.getId(), entityType,entityId);
        return CommunityUtil.parseJson("0", "已取消关注");
    }
    @RequestMapping(path = "/getFolloweeList/{userId}",method = RequestMethod.GET)
    public String getFolloweeList(@PathVariable("userId") int userId, Model model, Page page){
        page.setPath("/getFolloweeList/"+userId);
        page.setLimit(5);
        page.setRows((int) followService.followeeCount(userId,ENTITY_TYPE_USER));
        List<Map<String, Object>> followee = followService.findFollowee(userId, page.getOffset(), page.getLimit());
        User user = userService.getUserById(userId);
        model.addAttribute("users",user);
        model.addAttribute("followee",followee);
        model.addAttribute("hasFollowed",hasFollowed(userId));
        return "/site/followee";
    }
    @RequestMapping(path = "/getFollowerList/{userId}",method = RequestMethod.GET)
    public String getFollowerList(@PathVariable("userId") int userId, Model model, Page page){
        page.setPath("/getFollowerList/"+userId);
        page.setLimit(5);
        page.setRows((int) followService.followerCount(userId,ENTITY_TYPE_USER));
        List<Map<String, Object>> followee = followService.findFollower(userId, page.getOffset(), page.getLimit());
        User user = userService.getUserById(userId);
        model.addAttribute("users",user);
        model.addAttribute("follower",followee);
        model.addAttribute("hasFollowed",hasFollowed(userId));
        return "/site/follower";
    }
    private boolean hasFollowed(int userId){
        if(hostHolder.getUser()==null){
            return false;
        }
        return followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
    }
}
