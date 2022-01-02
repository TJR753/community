package com.example.community.controller;

import com.example.community.domain.DiscussPost;
import com.example.community.domain.User;
import com.example.community.domain.vo.Page;
import com.example.community.service.DiscussPostService;
import com.example.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;

    @RequestMapping(path="/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){
        int rows=discussPostService.findDiscussPostRows(0);
        page.setPath("/index");
        page.setRows(rows);
        //userId,offset,pageSize,不展示status为2的,按照type,createTime排序,分页获得总条数
        List<DiscussPost> discussPostList=discussPostService.findDiscussPosts(0,page.getOffset(),page.getLimit());

        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for(DiscussPost dp:discussPostList){
            HashMap<String, Object> map = new HashMap<>();
            User user=userService.getUserById(dp.getUserId());
            map.put("user",user);
            map.put("discussPost",dp);
            list.add(map);
        }
        model.addAttribute("discussPostList",list);
        return "index";
    }
}
