package com.example.community.quartz;

import com.example.community.domain.DiscussPost;
import com.example.community.service.CommentService;
import com.example.community.service.DiscussPostService;
import com.example.community.service.ElasticsearchService;
import com.example.community.service.LikeService;
import com.example.community.utils.CommunityConstant;
import com.example.community.utils.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostScoreRefreshJob implements Job, CommunityConstant {
    private static final Logger logger= LoggerFactory.getLogger(PostScoreRefreshJob.class);

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private ElasticsearchService elasticsearchService;

    private static final Date epoch;
    static {
        try {
            epoch=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-08-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("牛客纪元初始化失败",e);
        }
    }
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        BoundSetOperations boundSetOps = redisTemplate.boundSetOps(RedisKeyUtil.getPostScoreKey());
        if(boundSetOps.size()==0){
            logger.info("没有需要刷新的帖子");
            return;
        }
        logger.info("[任务开始] 开始刷新帖子分数");
        while(boundSetOps.size()>0){
            int postId = (int)boundSetOps.pop();
            //精华分
            DiscussPost post = discussPostService.findDiscussPostById(postId+"");
            if(post==null){
                logger.error("帖子不存在");
                return;
            }
            int statusScore=post.getStatus()==1?75:0;
            //评论数
            Integer commentCount = post.getCommentCount();
            //点赞数
            long likeCount = likeService.likeCount(ENTITY_TYPE_POST, postId);
            //时间
            long day=(post.getCreateTime().getTime()-epoch.getTime())/(1000*60*60*24);
            //计算权重
            double w=statusScore+commentCount*10+likeCount*2;
            double score=Math.log(Math.max(w,1))+day;
            discussPostService.updateScore(score,postId);
            post.setScore(score);
            elasticsearchService.saveDiscussPost(post);
        }
        logger.info("[任务结束] 帖子分数刷新完毕");
    }
}
