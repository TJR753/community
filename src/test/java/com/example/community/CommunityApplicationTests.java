package com.example.community;

import com.example.community.domain.DiscussPost;
import com.example.community.domain.LoginTicket;
import com.example.community.domain.User;
import com.example.community.mapper.DiscussPostMapper;
import com.example.community.mapper.LoginTicketMapper;
import com.example.community.mapper.UserMapper;
import com.example.community.service.DiscussPostService;
import com.example.community.service.Impl.TestService;
import com.example.community.service.UserService;
import com.example.community.utils.CommunityUtil;
import com.example.community.utils.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Autowired
    private TestService testService;
    @Test
    public void test(){
        List<DiscussPost> discussPostList=discussPostService.findDiscussPosts(0,0,10);
        int total=discussPostService.findDiscussPostRows(0);
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for(DiscussPost dp:discussPostList){
            HashMap<String, Object> map = new HashMap<>();
            User user=userService.getUserById(dp.getUserId());
            map.put("user",user);
            map.put("discussPost",dp);
            list.add(map);
        }
    }
    @Test
    public void test1(){
        LoginTicket ticket = new LoginTicket();
        ticket.setTicket("123");
        ticket.setExpired(new Date());
        ticket.setUserId(1234);
        loginTicketMapper.insert(ticket);
    }
    @Test
    public void test2(){
        String p="123";
        String s = CommunityUtil.generateUUID().substring(0, 5);
        String s1 = CommunityUtil.md5(p + s);
        userMapper.updatePasswordByEmail("123@qq.com",s1,s);
    }
    @Test
    public void test3(){
        String text="这里可以赌博，也可以喝酒，吸烟。。。打架等等";
        String s = sensitiveFilter.filter(text);
        System.out.println(s);
    }
    @Test
    public void test4(){

    }
}
