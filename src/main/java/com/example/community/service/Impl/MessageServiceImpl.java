package com.example.community.service.Impl;

import com.example.community.domain.Message;
import com.example.community.mapper.MessageMapper;
import com.example.community.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageMapper messageMapper;
    @Override
    public List<Message> selectConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId,offset,limit);
    }

    @Override
    public int selectConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    @Override
    public List<Message> selectLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    @Override
    public int selectLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    @Override
    public int selectUnreadLetterCount(int userId, String conversationId) {
        return messageMapper.selectUnreadLetterCount(userId,conversationId);
    }

    @Override
    public int insertMessage(Message message) {
        return messageMapper.insertMessage(message);
    }

    @Override
    public int updateStatus(String userId,String conversationId) {
        return messageMapper.updateStatus(userId,conversationId);
    }

    @Override
    public int updateStatusById(int id) {
        return messageMapper.updateStatusById(id);
    }

    @Override
    public int findNoticeCount(String topic, int userId) {
        return messageMapper.findNoticeCount(topic,userId);
    }

    @Override
    public int findNoticeUnreadCount(String topic, int userId) {
        return messageMapper.findNoticeUnreadCount(topic,userId);
    }

    @Override
    public Message findLatestNotice(String topic, int userId) {
        return messageMapper.findLatestNotice(topic, userId);
    }

    @Override
    public List<Message> findNoticeByTopic(String topic, int userId, int offset, int limit) {
        return messageMapper.findNoticeByTopic(topic,userId,offset,limit);
    }

    @Override
    public int readMessage(String topic, int id){
        return messageMapper.readMessage(topic,id);
    }
}
