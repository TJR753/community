package com.example.community.controller;

import com.example.community.domain.Message;
import com.example.community.domain.User;
import com.example.community.domain.vo.Page;
import com.example.community.service.MessageService;
import com.example.community.service.UserService;
import com.example.community.utils.CommunityUtil;
import com.example.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/message")
public class MessageController {
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
        int totalUnread = messageService.selectUnreadLetterCount(user.getId(), null);
        ArrayList<Map<String,Object>> voList=new ArrayList<>();
        for(Message m:messages){
            HashMap<String, Object> map = new HashMap<>();
            int unreadLetterCount = messageService.selectUnreadLetterCount(user.getId(), m.getConversationId());
            int letterCount = messageService.selectLetterCount(m.getConversationId());
            map.put("message",m);
            map.put("unread",unreadLetterCount);
            map.put("read",letterCount);
            int fromId=user.getId()==m.getFromId()?m.getToId():m.getFromId();
            User user1 = userService.getUserById(fromId+"");
            map.put("from",user1);
            voList.add(map);
        }
        model.addAttribute("vo",voList);
        model.addAttribute("totalUnread",totalUnread);
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
            User from = userService.getUserById(m.getFromId()+"");
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
            return userService.getUserById(id2+"");
        }else{
            return userService.getUserById(id1+"");
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
}
