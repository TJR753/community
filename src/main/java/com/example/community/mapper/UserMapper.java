package com.example.community.mapper;

import com.example.community.domain.User;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    User getUserById(String userId);

    User getUserByUserName(String username);

    User getUserByEmail(String email);

    int insertUser(User user);

    void changeStatus(String s,String id);

    @Update("update user set salt=#{salt},password=#{md5} where email=#{email}")
    void updatePasswordByEmail(String email, String md5, String salt);
}
