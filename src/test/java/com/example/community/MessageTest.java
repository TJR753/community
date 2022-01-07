package com.example.community;

import com.example.community.mapper.MessageMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class MessageTest {
    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void test01(){
        System.out.println(messageMapper.selectConversations(111, 0, 20));
        System.out.println(messageMapper.selectConversationCount(111));
        System.out.println(messageMapper.selectLetters("111_112",0,20));
        System.out.println(messageMapper.selectLetterCount("111_112"));
    }
    @Test
    public void test02(){
        System.out.println(messageMapper.selectUnreadLetterCount(111,null));
    }
}
