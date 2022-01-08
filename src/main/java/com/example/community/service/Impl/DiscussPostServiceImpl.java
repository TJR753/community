package com.example.community.service.Impl;

import com.example.community.domain.DiscussPost;
import com.example.community.domain.User;
import com.example.community.mapper.DiscussPostMapper;
import com.example.community.service.DiscussPostService;
import com.example.community.utils.CommunityUtil;
import com.example.community.utils.HostHolder;
import com.example.community.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;
import java.util.List;

@Service
public class DiscussPostServiceImpl implements DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Autowired
    private HostHolder hostHolder;
    @Override
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        List<DiscussPost> discussPostList=discussPostMapper.findDiscussPosts(userId,offset,limit);
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
}
