package com.example.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.community.domain.Message;
import com.example.community.domain.User;
import com.example.community.domain.vo.Page;
import com.example.community.service.MessageService;
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
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
@RequestMapping("/message")
public class MessageController implements CommunityConstant {
    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @RequestMapping(path = "/getLetterPage",method = RequestMethod.GET)
    public String showFeiendMessage(Model model, Page page){
        User user = hostHolder.getUser();
        page.setLimit(5);
        page.setRows(messageService.selectConversationCount(user.getId()));
        page.setPath("/message/getLetterPage");
        List<Message> messages = messageService.selectConversations(user.getId(), page.getOffset(), page.getLimit());
        ArrayList<Map<String,Object>> voList=new ArrayList<>();
        for(Message m:messages){
            HashMap<String, Object> map = new HashMap<>();
            int unreadLetterCount = messageService.selectUnreadLetterCount(user.getId(), m.getConversationId());
            int letterCount = messageService.selectLetterCount(m.getConversationId());
            map.put("message",m);
            map.put("unread",unreadLetterCount);
            map.put("read",letterCount);
            int fromId=user.getId()==m.getFromId()?m.getToId():m.getFromId();
            User user1 = userService.getUserById(fromId);
            map.put("from",user1);
            voList.add(map);
        }
        model.addAttribute("vo",voList);
        int unReadNoticeCount = messageService.findNoticeUnreadCount(null, user.getId());
        int unreadLetterCount = messageService.selectUnreadLetterCount(user.getId(), null);
        model.addAttribute("unReadNoticeCount",unReadNoticeCount);
        model.addAttribute("unreadLetterCount",unreadLetterCount);
        return "/site/letter";
    }
    @RequestMapping(path = "/getLetterDetail/{conversationId}",method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId")String conversationId,Model model,Page page){
        page.setRows(messageService.selectLetterCount(conversationId));
        page.setLimit(5);
        page.setPath("/message/getLetterDetail/"+conversationId);
        User user = hostHolder.getUser();
        List<Message> messages = messageService.selectLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String,Object>> voList=new ArrayList<>();
        for(Message m:messages){
            HashMap<String, Object> map = new HashMap<>();
            User from = userService.getUserById(m.getFromId());
            map.put("from",from);
            map.put("message",m);
            voList.add(map);
        }
        model.addAttribute("vo",voList);
        model.addAttribute("target",getTargetUser(conversationId));
        messageService.updateStatus(user.getId()+"",conversationId);
        return "/site/letter-detail";
    }

    public User getTargetUser(String conversationId){
        String[] s = conversationId.split("_");
        int id1=Integer.parseInt(s[0]);
        int id2=Integer.parseInt(s[1]);
        if(hostHolder.getUser().getId()==id1){
            return userService.getUserById(id2);
        }else{
            return userService.getUserById(id1);
        }
    }
    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String add(Message message,String toName){
        User user = hostHolder.getUser();
        message.setFromId(user.getId());
        message.setToId(userService.getUserByName(toName));
        message.setConversationId(message.getFromId()> message.getToId()?
                message.getToId()+"_"+ message.getFromId():message.getFromId()+"_"+ message.getToId());
        message.setStatus(0);
        message.setCreateTime(new Date());
        System.out.println(message);
        messageService.insertMessage(message);
        return CommunityUtil.parseJson("0","发送成功");
    }
    @RequestMapping(path="/updateStatusById",method = RequestMethod.POST)
    @ResponseBody
    public String updateStatusById(int id){
        messageService.updateStatusById(id);
        return CommunityUtil.parseJson("0","发送成功");
    }
    @RequestMapping(path = "/getNotice",method = RequestMethod.GET)
    public String getNotice(Model model){
        User user = hostHolder.getUser();
        //评论
        Message latestNotice = messageService.findLatestNotice(TOPIC_COMMENT, user.getId());
        HashMap<String, Object> messageVO=null;
        if(latestNotice!=null){
            messageVO=new HashMap<>();
            messageVO.put("message",latestNotice);
            String content = HtmlUtils.htmlUnescape(latestNotice.getContent());
            HashMap<String,Object> map = JSONObject.parseObject(content, HashMap.class);
            messageVO.put("user",userService.getUserById((Integer) map.get("userId")));
            messageVO.put("entityType",map.get("entityType"));
            messageVO.put("entityId",map.get("entityId"));
            messageVO.put("postId",map.get("postId"));
            int noticeCount = messageService.findNoticeCount(TOPIC_COMMENT, user.getId());
            int noticeUnreadCount = messageService.findNoticeUnreadCount(TOPIC_COMMENT, user.getId());
            messageVO.put("noticeCount",noticeCount);
            messageVO.put("noticeUnreadCount",noticeUnreadCount);
        }
        model.addAttribute("commentNotice",messageVO);
        //赞
        latestNotice = messageService.findLatestNotice(TOPIC_COMMENT, user.getId());

        if(latestNotice!=null){
            messageVO = new HashMap<>();
            messageVO.put("message",latestNotice);
            String content = HtmlUtils.htmlUnescape(latestNotice.getContent());
            HashMap<String,Object> map = JSONObject.parseObject(content, HashMap.class);
            messageVO.put("user",userService.getUserById((Integer) map.get("userId")));
            messageVO.put("entityType",map.get("entityType"));
            messageVO.put("entityId",map.get("entityId"));
            messageVO.put("postId",map.get("postId"));
            int noticeCount = messageService.findNoticeCount(TOPIC_LIKE, user.getId());
            int noticeUnreadCount = messageService.findNoticeUnreadCount(TOPIC_LIKE, user.getId());
            messageVO.put("noticeCount",noticeCount);
            messageVO.put("noticeUnreadCount",noticeUnreadCount);
        }
        model.addAttribute("likeNotice",messageVO);
        //关注
        latestNotice = messageService.findLatestNotice(TOPIC_COMMENT, user.getId());
        if(latestNotice!=null){
            messageVO = new HashMap<>();
            messageVO.put("message",latestNotice);
            String content = HtmlUtils.htmlUnescape(latestNotice.getContent());
            HashMap<String,Object> map = JSONObject.parseObject(content, HashMap.class);
            messageVO.put("user",userService.getUserById((Integer) map.get("userId")));
            messageVO.put("entityType",map.get("entityType"));
            messageVO.put("entityId",map.get("entityId"));
            messageVO.put("postId",map.get("postId"));
            int noticeCount = messageService.findNoticeCount(TOPIC_FOLLOW, user.getId());
            int noticeUnreadCount = messageService.findNoticeUnreadCount(TOPIC_FOLLOW, user.getId());
            messageVO.put("noticeCount",noticeCount);
            messageVO.put("noticeUnreadCount",noticeUnreadCount);
        }
        int unReadNoticeCount = messageService.findNoticeUnreadCount(null, user.getId());
        int unreadLetterCount = messageService.selectUnreadLetterCount(user.getId(), null);

        model.addAttribute("followNotice",messageVO);
        model.addAttribute("unReadNoticeCount",unReadNoticeCount);
        model.addAttribute("unreadLetterCount",unreadLetterCount);
        return "/site/notice";
    }
    @RequestMapping(path = "/getNoticeDetail/{topic}",method = RequestMethod.GET)
    public String getNoticeDetail(@PathVariable("topic")String topic,Model model,Page page){
        User user = hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/message/getNoticeDetail/"+topic);
        page.setRows(messageService.findNoticeCount(topic,user.getId()));

        List<Message> messageList=messageService.findNoticeByTopic(topic,user.getId(),page.getOffset(),page.getLimit());
        List<Map<String,Object>> messageVo=new ArrayList<>();
        if(messageList!=null){
            for(Message m:messageList){
                HashMap<String, Object> map = new HashMap<>();
                map.put("message",m);
                String content = HtmlUtils.htmlUnescape(m.getContent());
                HashMap data = JSONObject.parseObject(content, HashMap.class);
                map.put("user",user);
                map.put("entityType",data.get("entityType"));
                map.put("entityId",data.get("entityId"));
                map.put("postId",data.get("postId"));
                //通知作者
                map.put("fromUser",userService.getUserById(m.getFromId()));
                messageVo.add(map);
            }
        }
        model.addAttribute("notices",messageVo);
        //设置以读
       messageService.readMessage(topic,user.getId());
       return "/site/notice-detail";
    }
}
