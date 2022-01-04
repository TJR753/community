package com.example.community.controller;

import com.example.community.domain.User;
import com.example.community.service.UserService;
import com.example.community.utils.CommunityConstant;
import com.example.community.utils.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

@Controller
public class RegisterController implements CommunityConstant {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/register",method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    //username,password,email
    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String,Object> map=userService.register(user);
        if(map==null||map.isEmpty()){
            model.addAttribute("msg","注册成功，我们向你的邮箱发送了一封激活邮件，请尽快激活");
            model.addAttribute("target","/index");
            return "site/operate-result";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "site/register";
        }
    }
    //激活
    @RequestMapping(path="/activation/{id}/{code}")
    public String activation(Model model, @PathVariable("id")String id,@PathVariable("code")String code){
        int i=userService.activation(id,code);
        if(i==ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功，您的账户可以正常使用了");
            model.addAttribute("target","/login");
        }else if(i==ACTIVATION_FAIL){
            model.addAttribute("msg","激活失败，你提供的激活码不正确");
            model.addAttribute("target","/index");
        }else{
            model.addAttribute("msg","无效操作，该账号已经激活");
            model.addAttribute("target","/login");
        }
        return "site/operate-result";
    }
}
