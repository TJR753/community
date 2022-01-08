package com.example.community.service.Impl;

import com.example.community.service.LikeService;
import com.example.community.utils.HostHolder;
import com.example.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private HostHolder hostHolder;

    /**
     *
     * @param userId 当前用户id
     * @param entityType 实体类型
     * @param entityId 实体id
     * @param entityUserId 点赞的实体作者id
     */
    @Override
    public void like(int userId, int entityType, int entityId,int entityUserId) {
//        String likeKey = LikeKeyUtil.getEntityLikeKey(entityType, entityId);
//        if(redisTemplate.opsForSet().isMember(likeKey,userId)){
//            redisTemplate.opsForSet().remove(likeKey,userId);
//        }else{
//            redisTemplate.opsForSet().add(likeKey,userId);
//        }
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        String userLikeKey= RedisKeyUtil.getUserEntityKey(entityUserId);
        Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                redisTemplate.multi();
                if(isMember){
                    redisTemplate.opsForSet().remove(entityLikeKey,userId);
                    redisTemplate.opsForValue().decrement(userLikeKey);
                }
                else{
                    redisTemplate.opsForSet().add(entityLikeKey,userId);
                    redisTemplate.opsForValue().increment(userLikeKey);
                }
                return redisTemplate.exec();
            }
        });
    }

    @Override
    public long likeCount(int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(likeKey);
    }

    @Override
    public int likeStatus(int userId,int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(likeKey,userId)?1:0;
    }

    @Override
    public int userLikeCount(int userId) {
        String userLikeKey= RedisKeyUtil.getUserEntityKey(userId);
        Object o = redisTemplate.opsForValue().get(userLikeKey);
        return o==null?0:(int)o;
    }
}
