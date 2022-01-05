package com.example.community.service;

import com.example.community.domain.LoginTicket;
import com.example.community.domain.User;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Update;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public interface UserService {
    User getUserById(String userId);

    Map<String, Object> register(User user);

    int activation(String id,String code);

    HashMap<String, Object> login(String username, String password, int expeiredSeconds);

    void logout(String ticket);

    HashMap<String, Object> sendVerifyCode(String email);

    void updatePassword(String email, String password);

    LoginTicket getLoginTicket(String ticket);

    void updateHeaderUrl(int id, String headerUrl);

    HashMap<String, Object> setPassword(String password, String newPassword, String newSecondPassword);
}
