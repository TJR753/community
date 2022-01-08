package com.example.community.service.Impl;

import com.example.community.domain.User;
import com.example.community.service.FollowService;
import com.example.community.service.UserService;
import com.example.community.utils.CommunityConstant;
import com.example.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowServiceImpl implements FollowService,CommunityConstant{
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserService userService;

    @Override
    public void follow(int userId, int entityType, int entityId) {
        String followeeEntityKey = RedisKeyUtil.getFolloweeEntityKey(userId, entityType);
        String followerEntityKey = RedisKeyUtil.getFollowerEntityKey(entityType, entityId);
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                redisTemplate.multi();
                redisTemplate.opsForZSet().add(followeeEntityKey,entityId,System.currentTimeMillis());
                redisTemplate.opsForZSet().add(followerEntityKey,userId,System.currentTimeMillis());
                return redisTemplate.exec();
            }
        });
    }

    @Override
    public void unfollow(int userId, int entityType, int entityId) {
        String followeeEntityKey = RedisKeyUtil.getFolloweeEntityKey(userId, entityType);
        String followerEntityKey = RedisKeyUtil.getFollowerEntityKey(entityType, entityId);
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                redisTemplate.multi();
                redisTemplate.opsForZSet().remove(followeeEntityKey,entityId);
                redisTemplate.opsForZSet().remove(followerEntityKey,userId);
                return redisTemplate.exec();
            }
        });
    }

    @Override
    public long followeeCount(int userId, int entityType) {
        String followeeEntityKey = RedisKeyUtil.getFolloweeEntityKey(userId, entityType);
        return redisTemplate.opsForZSet().size(followeeEntityKey);
    }

    @Override
    public long followerCount(int entityId, int entityType) {
        String followerEntityKey = RedisKeyUtil.getFollowerEntityKey(entityId, entityType);
        return redisTemplate.opsForZSet().zCard(followerEntityKey);
    }

    @Override
    public boolean hasFollowed(int userId, int entityType, int entityId) {
        String followerEntityKey = RedisKeyUtil.getFollowerEntityKey(entityType, entityId);
        return redisTemplate.opsForZSet().score(followerEntityKey,userId)!=null;
    }

    @Override
    public List<Map<String, Object>> findFollowee(int userId, int offset, int limit) {
        String followeeEntityKey = RedisKeyUtil.getFolloweeEntityKey(userId, ENTITY_TYPE_USER);
        Set<Integer> set = redisTemplate.opsForZSet().reverseRange(followeeEntityKey, offset, offset + limit - 1);
        if(set==null){
            return null;
        }
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for(Integer targetId:set){
            HashMap<String, Object> map = new HashMap<>();
            Double time = redisTemplate.opsForZSet().score(followeeEntityKey, targetId);
            User user = userService.getUserById(targetId);
            map.put("followee",user);
            map.put("followeeTime",new Date(time.longValue()));
            list.add(map);
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> findFollower(int userId, int offset, int limit) {
        String followerEntityKey = RedisKeyUtil.getFollowerEntityKey(ENTITY_TYPE_USER, userId);
        Set<Integer> set = redisTemplate.opsForZSet().reverseRange(followerEntityKey, offset, offset + limit - 1);
        if(set==null){
            return null;
        }
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for(Integer targetId:set){
            HashMap<String, Object> map = new HashMap<>();
            Double time = redisTemplate.opsForZSet().score(followerEntityKey, targetId);
            User user = userService.getUserById(targetId);
            map.put("follower",user);
            map.put("followerTime",new Date(time.longValue()));
            list.add(map);
        }
        return list;
    }
}
