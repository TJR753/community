package com.example.community.service.Impl;

import com.example.community.domain.User;
import com.example.community.mapper.UserMapper;
import com.example.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUserById(String userId) {
        User user=userMapper.getUserById(userId);
        return user;
    }
}
