package com.example.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testKey(){
        String redisKey="test:count";
        redisTemplate.opsForValue().set(redisKey,1);
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
        System.out.println(redisTemplate.opsForValue().get(redisKey));
    }
    @Test
    public void testList(){
        String redisKey="test:list";
//        redisTemplate.opsForList().leftPush(redisKey,101);
//        redisTemplate.opsForList().leftPush(redisKey,102);
//        redisTemplate.opsForList().leftPush(redisKey,103);
        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().range(redisKey, 0, -1));
    }
    @Test
    public void testSet(){
        String redisKey="test:set";
        redisTemplate.opsForSet().add(redisKey,1,2,4,5);
        System.out.println(redisTemplate.opsForSet().members(redisKey));
        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
    }
    @Test
    public void testHash() {
        String redisKey="test:hash";
        redisTemplate.opsForHash().put(redisKey,"id","123");
        redisTemplate.opsForHash().put(redisKey,"username","zhangsan");
        System.out.println(redisTemplate.opsForHash().get(redisKey, "id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey, "username"));
        redisTemplate.opsForHash().size(redisKey);
        System.out.println(redisTemplate.opsForHash().values(redisKey));
    }
    @Test
    public void testZSet(){
        String redisKey="test:zset";
        redisTemplate.opsForZSet().add(redisKey,"zhangsan",100);
        redisTemplate.opsForZSet().add(redisKey,"lisi",100);
        redisTemplate.opsForZSet().add(redisKey,"wangwu",100);
        System.out.println(redisTemplate.opsForZSet().size(redisKey));
        System.out.println(redisTemplate.opsForZSet().range(redisKey, 0, -1));
        System.out.println(redisTemplate.opsForZSet().rank(redisKey, 1));
    }
    @Test
    public void HyperLogLogTest(){
        String redisKey="test:hll:01";
        for(int i=0;i<10000;i++){
            redisTemplate.opsForHyperLogLog().add(redisKey,i);
        }
        for(int i=0;i<10000;i++){
            int r=new Random().nextInt(10000);
            redisTemplate.opsForHyperLogLog().add(redisKey,r);
        }
        System.out.println(redisTemplate.opsForHyperLogLog().size(redisKey));
    }
    @Test
    public void HyperLogLogRepeatTest(){
        String redisKey2="test:hll:02";
        String redisKey3="test:hll:03";
        for(int i=0;i<10000;i++){
            redisTemplate.opsForHyperLogLog().add(redisKey2,i);
        }
        for(int i=5000;i<10000;i++){
            int r=new Random().nextInt(10000);
            redisTemplate.opsForHyperLogLog().add(redisKey3,r);
        }
        String unionRedisKey="test:hll:union";
        redisTemplate.opsForHyperLogLog().union(unionRedisKey,redisKey2,redisKey3);
        System.out.println(redisTemplate.opsForHyperLogLog().size(unionRedisKey));
    }
    @Test
    public void BitMapTest(){
        String RedisKey1="test:bitmap:1";
        String RedisKey2="test:bitmap:2";
        String RedisKey3="test:bitmap:3";
        redisTemplate.opsForValue().setBit(RedisKey1,0,true);
        redisTemplate.opsForValue().setBit(RedisKey1,1,true);
        redisTemplate.opsForValue().setBit(RedisKey1,2,true);
        redisTemplate.opsForValue().setBit(RedisKey2,4,true);
        redisTemplate.opsForValue().setBit(RedisKey2,5,true);
        redisTemplate.opsForValue().setBit(RedisKey3,6,true);
        String RedisKey="test:bitmap";
        Object o = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.bitOp(RedisStringCommands.BitOperation.OR, RedisKey.getBytes(), RedisKey1.getBytes(),
                        RedisKey2.getBytes(), RedisKey3.getBytes());
                return connection.bitCount(RedisKey.getBytes());
            }
        });
        System.out.println(redisTemplate.opsForValue().getBit(RedisKey,0));
        System.out.println(redisTemplate.opsForValue().getBit(RedisKey,1));
        System.out.println(redisTemplate.opsForValue().getBit(RedisKey,2));
        System.out.println(redisTemplate.opsForValue().getBit(RedisKey,3));
        System.out.println(redisTemplate.opsForValue().getBit(RedisKey,4));
        System.out.println(redisTemplate.opsForValue().getBit(RedisKey,5));
        System.out.println(redisTemplate.opsForValue().getBit(RedisKey,6));
    }
}
