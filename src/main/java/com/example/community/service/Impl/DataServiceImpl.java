package com.example.community.service.Impl;

import com.example.community.service.DataService;
import com.example.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Administrator
 */
@Service
public class DataServiceImpl implements DataService {
    @Autowired
    private RedisTemplate redisTemplate;
    private SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
    /**
     * 存入redis
     * @param ip 用户访问ip
     */
    @Override
    public void recordUV(String ip) {
        String s = sdf.format(new Date());
        String uvKey = RedisKeyUtil.getUVKey(s);
        redisTemplate.opsForHyperLogLog().add(uvKey,ip);
    }

    /**
     *
     * @param start 开始日期
     * @param end 结束日期
     * @return 独立访客数量
     */
    @Override
    public Long countUV(Date start, Date end) {
        if(start==null||end==null){
            return 0L;
        }
        Calendar instance = Calendar.getInstance();
        instance.setTime(start);
        ArrayList<String> list = new ArrayList<>();
        while(!instance.getTime().after(end)){
            String uvKey = RedisKeyUtil.getUVKey(sdf.format(instance.getTime()));
            list.add(uvKey);
            instance.add(Calendar.DATE,1);
        }
        String redisKey=RedisKeyUtil.getUVKey(sdf.format(start), sdf.format(end));
        redisTemplate.opsForHyperLogLog().union(redisKey, list.toArray());
        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }

    /**
     * 储存用户id
     * @param userId 用户id
     */
    @Override
    public void recordDAU(int userId) {
        String dauKey = RedisKeyUtil.getDAUKey(sdf.format(new Date()));
        redisTemplate.opsForValue().setBit(dauKey,userId,true);
    }

    @Override
    public Long countDAU(Date start, Date end) {
        if(start==null||end==null){
            return 0L;
        }
        String dauKey = RedisKeyUtil.getDAUKey(sdf.format(start), sdf.format(end));
        Calendar instance = Calendar.getInstance();
        instance.setTime(start);
        ArrayList<String> list = new ArrayList<>();
        while(!instance.getTime().after(end)){
            String dauKey1 = RedisKeyUtil.getDAUKey(sdf.format(instance.getTime()));
            list.add(dauKey1);
            instance.add(Calendar.DATE,1);
        }

        return (long)redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.bitOp(RedisStringCommands.BitOperation.OR, dauKey.getBytes(), list.toArray(new byte[0][0]));
                return connection.bitCount(dauKey.getBytes());
            }
        });
    }
}
