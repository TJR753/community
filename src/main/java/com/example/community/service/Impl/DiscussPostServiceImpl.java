package com.example.community.service.Impl;

import com.example.community.domain.DiscussPost;
import com.example.community.domain.Event;
import com.example.community.domain.User;
import com.example.community.event.EventProducer;
import com.example.community.mapper.DiscussPostMapper;
import com.example.community.mapper.elasticsearch.DiscussPostRepository;
import com.example.community.service.DiscussPostService;
import com.example.community.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;
import java.util.List;

@Service
public class DiscussPostServiceImpl implements DiscussPostService, CommunityConstant {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private DiscussPostRepository discussPostRepository;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit,int orderMode) {
        List<DiscussPost> discussPostList=discussPostMapper.findDiscussPosts(userId,offset,limit,orderMode);
        return discussPostList;
    }

    @Override
    public int findDiscussPostRows(int userId) {
        int total=discussPostMapper.findDiscussRows(userId);
        return total;
    }

    @Override
    public String addDiscussPost(String title, String content) {
        //判断用户是否登录
        User user = hostHolder.getUser();
        if(user==null){
            String json = CommunityUtil.parseJson("403", "请先登录");
            return json;
        }

        String titleHtml = sensitiveFilter.filter(HtmlUtils.htmlEscape(title));
        String contentHtml = sensitiveFilter.filter(HtmlUtils.htmlEscape(content));

        DiscussPost discussPost = new DiscussPost();
        discussPost.setTitle(titleHtml);
        discussPost.setContent(contentHtml);
        discussPost.setUserId(user.getId());
        discussPost.setType(0);
        discussPost.setStatus(0);
        discussPost.setCreateTime(new Date());
        discussPost.setCommentCount(0);
        discussPost.setScore(0.0);
        discussPostMapper.addDiscussPost(discussPost);
        String json = CommunityUtil.parseJson("0", "发布成功");
        //发帖触发es事件
        Event event = new Event().setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(discussPost.getType())
                .setEntityId(discussPost.getId())
                .setEntityUserId(discussPost.getUserId())
                .setData("postId", discussPost.getId());
        eventProducer.fireEvent(event);
        redisTemplate.opsForSet().add(RedisKeyUtil.getPostScoreKey(),discussPost.getId());
        return json;
    }

    @Override
    public DiscussPost findDiscussPostById(String id) {
        DiscussPost discussPost=discussPostMapper.findDiscussPostById(id);
        return discussPost;
    }

    @Override
    public List<DiscussPost> getMyPost(int userId) {
        return discussPostMapper.getMyPost(userId);
    }

    @Override
    public int updateType(int id) {
        discussPostMapper.updateType(id);
        DiscussPost discussPost = discussPostMapper.findDiscussPostById(id+"");
        User user = hostHolder.getUser();
        //发帖触发es事件
        Event event = new Event().setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(discussPost.getType())
                .setEntityId(discussPost.getId())
                .setEntityUserId(discussPost.getUserId())
                .setData("postId", discussPost.getId());
        eventProducer.fireEvent(event);
        return 0;
    }

    @Override
    public int updateStatus(int id,int status) {
        DiscussPost discussPost = discussPostMapper.findDiscussPostById(id+"");
        User user = hostHolder.getUser();
        //发帖触发es事件
        Event event = new Event().setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(discussPost.getType())
                .setEntityId(discussPost.getId())
                .setEntityUserId(discussPost.getUserId())
                .setData("postId", discussPost.getId());
        eventProducer.fireEvent(event);
        //储存需要更新的帖子id
        String postScoreKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(postScoreKey,id);
        return discussPostMapper.updateStatus(id,status);
    }

    @Override
    public int updateScore(double score, int postId) {
        return discussPostMapper.updateScore(score,postId);
    }
}
