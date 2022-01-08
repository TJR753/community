package com.example.community.service.Impl;

import com.example.community.domain.LoginTicket;
import com.example.community.domain.User;
import com.example.community.mapper.LoginTicketMapper;
import com.example.community.mapper.UserMapper;
import com.example.community.service.UserService;
import com.example.community.utils.*;
import com.google.code.kaptcha.Producer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import sun.security.krb5.internal.Ticket;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService, CommunityConstant {
    @Autowired
    private UserMapper userMapper;
    @Autowired
   private TemplateEngine templateEngine;
    @Autowired
    private MailClient mailClient;
//    @Autowired
//    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private Producer kaptchaProducer;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Override
    public User getUserById(int userId) {
        User user = getUserCaChe(userId);
        if(user==null){
            user = initUserCache(userId);
        }
        return user;
    }

    @Override
    public Map<String, Object> register(User user) {
        HashMap<String, Object> map = new HashMap<>();
        //判空处理
        if(user==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }
        //验证用户，根据用户名
        User user1=userMapper.getUserByUserName(user.getUsername());
        if(user1!=null){
            map.put("usernameMsg","用户已存在");
            return map;
        }
        //验证邮箱
        User user2=userMapper.getUserByEmail(user.getEmail());
        if(user2!=null){
            map.put("emailMsg","邮箱已被注册");
            return map;
        }
        //添加用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setHeaderUrl(String.format("http://images.newcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        user.setActivationCode(CommunityUtil.generateUUID());
        userMapper.insertUser(user);
        System.out.println(user.getId());
        //发送激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        String url=domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendHtmlMessage(user.getEmail(),"激活邮件",content);
        return map;
    }

    @Override
    public int activation(String id,String code) {
        User user = userMapper.getUserById(id);
        if(user.getStatus()==1){
            return ACTIVATION_REPEAT;
        }else{
            if(code.equals(user.getActivationCode())){
                userMapper.changeStatus("1",id);
                clearUserCache(Integer.valueOf(id));
                return ACTIVATION_SUCCESS;
            }else{
                return ACTIVATION_FAIL;
            }
        }
    }

    @Override
    public HashMap<String, Object> login(String username, String password, int expeiredSeconds) {
        HashMap<String, Object> map = new HashMap<>();
        //判空
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        User user = userMapper.getUserByUserName(username);
        if(user==null){
            map.put("usernameMsg","账号不存在");
            return map;
        }
        if(!user.getPassword().equals(CommunityUtil.md5(password+user.getSalt()))){
            map.put("passwordMsg","密码错误");
            return map;
        }
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(user.getId());
        ticket.setStatus(0);
        ticket.setTicket(CommunityUtil.generateUUID());
        ticket.setExpired(new Date(System.currentTimeMillis()+expeiredSeconds*1000));
//        loginTicketMapper.insert(ticket);
        String ticketKey = RedisKeyUtil.getTicketKey(ticket.getTicket());
        redisTemplate.opsForValue().set(ticketKey,ticket);
        map.put("ticket",ticket.getTicket());
        return map;
    }

    @Override
    public void logout(String ticket) {
//        loginTicketMapper.updateStatus(ticket);
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(ticketKey,loginTicket);
    }

    @Override
    public HashMap<String, Object> sendVerifyCode(String email) {
        HashMap<String, Object> map = new HashMap<>();
        //判空
        if(StringUtils.isBlank(email)){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }
        User user = userMapper.getUserByEmail(email);
        if(user==null){
            map.put("emailMsg","该邮箱未注册过");
            return map;
        }
        String code = kaptchaProducer.createText();
        map.put("code",code);
        return map;
    }

    @Override
    public void updatePassword(String email, String password) {
        String salt=CommunityUtil.generateUUID().substring(0,5);
        String md5 = CommunityUtil.md5(password + salt);
        userMapper.updatePasswordByEmail(email,md5,salt);
        clearUserCache(hostHolder.getUser().getId());
    }

    @Override
    public LoginTicket getLoginTicket(String ticket) {
//        LoginTicket loginTicket=loginTicketMapper.findTicket(ticket);
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
    }

    @Override
    public void updateHeaderUrl(int id, String headerUrl) {
        userMapper.updateHeaderUrl(id,headerUrl);
        clearUserCache(id);
    }

    @Override
    public HashMap<String, Object> setPassword(String password, String newPassword, String newSecondPassword) {
        HashMap<String, Object> map = new HashMap<>();
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","原始密码不能为空");
            return map;
        }
        if(StringUtils.isBlank(newPassword)){
            map.put("newPasswordMsg","新密码不能为空");
            return map;
        }
        if(StringUtils.isBlank(newSecondPassword)){
            map.put("newSecondPasswordMsg","确认密码不能为空");
            return map;
        }
        if(!newPassword.equals(newSecondPassword)){
            map.put("newPasswordMsg","两次输入密码不一致");
            return map;
        }
        User user = userMapper.getUserById(hostHolder.getUser().getId()+"");
        if(!user.getPassword().equals(CommunityUtil.md5(password+user.getSalt()))){
            map.put("passwordMsg","密码不正确");
            return map;
        }
        String salt = CommunityUtil.generateUUID().substring(0, 5);
        String md5 = CommunityUtil.md5(newPassword + salt);
        userMapper.updatePasswordByEmail(user.getEmail(),md5,salt);
        map.put("success",true);
        clearUserCache(hostHolder.getUser().getId());
        return map;
    }

    @Override
    public int getUserByName(String toName) {
        User user = userMapper.getUserByUserName(toName);
        return user.getId();
    }

    private User getUserCaChe(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        User user = (User) redisTemplate.opsForValue().get(userKey);
        if(user==null){
            user=initUserCache(userId);
        }
        return user;
    }
    private User initUserCache(int userId){
        User user = userMapper.getUserById(userId+"");
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(userKey,user,60, TimeUnit.SECONDS);
        return user;
    }
    private void clearUserCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(userKey);
    }
}
