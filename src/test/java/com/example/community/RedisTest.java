package com.example.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

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
    public void test(){
        Object a=1;
        System.out.println(a==null?0:(long)a);
    }

}
