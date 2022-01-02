package com.example.community.mapper;

import com.example.community.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    User getUserById(String userId);
}
