package com.example.community.service;

import com.example.community.domain.Message;
import org.springframework.stereotype.Service;

import java.util.List;

public interface MessageService {
    //查询当前用户的会话列表，只返回最新的一条
    List<Message> selectConversations(int userId, int offset, int limit);
    //查询当前用户的会话数量
    int selectConversationCount(int userId);
    //查询某个会话包含的私信列表
    List<Message> selectLetters(String conversationId,int offset,int limit);
    //查询某个会话包含的私信数量
    int selectLetterCount(String conversationId);
    //查询未读私信数量
    int selectUnreadLetterCount(int userId,String conversationId);

    int insertMessage(Message message);

    int updateStatus(String userId,String conversationId);

    int updateStatusById(int id);

    //某个主题的通知数量
    int findNoticeCount(String topic,int userId);
    //某个主题未读数量
    int findNoticeUnreadCount(String topic,int userId);
    //某个主题最新的一条信息
    Message findLatestNotice(String topic,int userId);

    List<Message> findNoticeByTopic(String topic, int userId, int offset, int limit);


    int readMessage(String topic, int id);
}
