package com.example.community.service.Impl;

import com.example.community.domain.DiscussPost;
import com.example.community.domain.Event;
import com.example.community.domain.User;
import com.example.community.event.EventProducer;
import com.example.community.mapper.DiscussPostMapper;
import com.example.community.mapper.elasticsearch.DiscussPostRepository;
import com.example.community.service.DiscussPostService;
import com.example.community.utils.*;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussPostServiceImpl implements DiscussPostService, CommunityConstant {
    private static final Logger logger= LoggerFactory.getLogger(DiscussPostServiceImpl.class);

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
    @Value(("${caffeine.posts.max-size}"))
    private long maxSize;
    @Value(("${caffeine.posts.expire-seconds}"))
    private long expireSeconds;

    /**
     * 缓存最热帖子列表
     */
    private LoadingCache<String,List<DiscussPost>> postsCache;
    /**
     * 缓存帖子总数
     */
    private LoadingCache<Integer,Integer> postRowsCache;

    /**
     * 初始化帖子列表和帖子总数缓存
     */
    @PostConstruct
    public void init(){
        postsCache= Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Override
                    public @Nullable List<DiscussPost> load(@NonNull String key) throws Exception {
                        if(key==null||key.length()==0){
                            throw new IllegalArgumentException("参数错误!");
                        }
                        String[] params = key.split(",");
                        if(params==null||params.length!=2){
                            throw new IllegalArgumentException("参数错误!");
                        }
                        int offset=Integer.valueOf(params[0]);
                        int limit=Integer.valueOf(params[1]);
                        logger.debug("load from DB");
                        return discussPostMapper.findDiscussPosts(0,offset,limit,1);
                    }
                });
        postRowsCache=Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds,TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public @Nullable Integer load(@NonNull Integer key) throws Exception {
                        logger.debug("load from DB");
                        return discussPostMapper.findDiscussRows(key);
                    }
                });
    }

    @Override
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit,int orderMode) {
//        只缓存最热，即当userId=0且orderMode=1
        if(userId==0&&orderMode==1){
            return postsCache.get(offset+","+limit);
        }
        logger.debug("load from DB");
        List<DiscussPost> discussPostList=discussPostMapper.findDiscussPosts(userId,offset,limit,orderMode);
        return discussPostList;
    }

    @Override
    public int findDiscussPostRows(int userId) {
        if(userId==0){
            return postRowsCache.get(userId);
        }
        logger.debug("load from DB");
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
