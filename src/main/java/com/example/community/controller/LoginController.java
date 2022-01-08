package com.example.community.controller;

import com.example.community.annotation.LoginRequired;
import com.example.community.domain.LoginTicket;
import com.example.community.service.UserService;
import com.example.community.utils.CommunityConstant;
import com.example.community.utils.CommunityUtil;
import com.example.community.utils.MailClient;
import com.example.community.utils.RedisKeyUtil;
import com.google.code.kaptcha.Producer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import sun.security.krb5.internal.Ticket;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController implements CommunityConstant {
    private static final Logger logger= LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private Producer kaptchaProducer;
    @Autowired
    private UserService userService;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String login(String username, String password, String code, boolean rememberMe/*,
                        HttpSession session*/, Model model,HttpServletResponse response,
                        @CookieValue("kaptchaOwner")String kaptchaOwner){
        String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        String verifyCode=null;
        if(StringUtils.isNoneBlank(kaptchaKey)){
            verifyCode = (String)redisTemplate.opsForValue().get(kaptchaKey);
        }
        if(StringUtils.isBlank(code)||StringUtils.isBlank(verifyCode)||!code.equalsIgnoreCase(verifyCode)){
            model.addAttribute("codeMsg","验证码不正确");
            return "/site/login";
        }
        int expeiredSeconds=rememberMe?REMEMBER_EXPIRED_SECONDS:EXPIRED_SECONDS;
        HashMap<String,Object> map=userService.login(username,password,expeiredSeconds);
        if(map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expeiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @RequestMapping(value = "/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response/* HttpSession session*/){
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        //验证码归属,发放给客户端的一个临时凭证
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        //将验证码存到缓存中,有效时间一分钟
        String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(kaptchaKey,text,60, TimeUnit.SECONDS);

        response.setContentType("image/png");
        try(ServletOutputStream os = response.getOutputStream();) {
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            logger.error("响应验证码失败"+e.getMessage());
        }
    }

    @LoginRequired
    @RequestMapping(value = "/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket")String ticket){
        userService.logout(ticket);
        return "redirect:/index";
    }

    @LoginRequired
    @RequestMapping(value = "/forget",method = RequestMethod.GET)
    public String getForgetPage(){
        return "/site/forget";
    }
    @RequestMapping(value = "/forget",method = RequestMethod.POST)
    public String forget(String email,String code,String password,String verifyCode,Model model){
        if(StringUtils.isBlank(code)||StringUtils.isBlank(verifyCode)||!code.equalsIgnoreCase(verifyCode)){
            model.addAttribute("codeMsg","验证码错误");
            model.addAttribute("email",email);
//            model.addAttribute("password",password);
            return "/site/forget";
        }
        userService.updatePassword(email,password);
        return "/site/login";
    }

    @LoginRequired
    @RequestMapping(path = "/sendVerifyCode",method = RequestMethod.GET)
    public String sendVerifyCode(String email,Model model){
        HashMap<String,Object> map=userService.sendVerifyCode(email);
        if(map.containsKey("code")){
            String code = (String)map.get("code");
            Context context = new Context();
            context.setVariable("code",code);
            context.setVariable("email",email);
            String content = templateEngine.process("/mail/forget", context);
            mailClient.sendHtmlMessage(email,"重置密码",content);
            model.addAttribute("code",code);
            model.addAttribute("email",email);
        }else{
            model.addAttribute("emailMsg",map.get("emailMsg"));
        }
        return "/site/forget::email";
    }
}
