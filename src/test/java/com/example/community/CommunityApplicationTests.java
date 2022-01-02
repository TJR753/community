package com.example.community;

import com.example.community.domain.DiscussPost;
import com.example.community.domain.User;
import com.example.community.mapper.DiscussPostMapper;
import com.example.community.mapper.UserMapper;
import com.example.community.service.DiscussPostService;
import com.example.community.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
