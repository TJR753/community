package com.example.community.controller;

import com.example.community.annotation.LoginRequired;
import com.example.community.domain.Comment;
import com.example.community.domain.DiscussPost;
import com.example.community.domain.User;
import com.example.community.domain.vo.Page;
import com.example.community.mapper.elasticsearch.DiscussPostRepository;
import com.example.community.service.*;
import com.example.community.utils.CommunityConstant;
import com.example.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private MessageService messageService;

    @RequestMapping(path="/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){
        int rows=discussPostService.findDiscussPostRows(0);
        page.setPath("/index");
        page.setRows(rows);
        //userId,offset,pageSize,不展示status为2的,按照type,createTime排序,分页获得总条数
        List<DiscussPost> discussPostList=discussPostService.findDiscussPosts(0,page.getOffset(),page.getLimit());

        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for(DiscussPost dp:discussPostList){
            HashMap<String, Object> map = new HashMap<>();
            User user=userService.getUserById(dp.getUserId());
            map.put("user",user);
            map.put("discussPost",dp);
            long likeCount = likeService.likeCount(ENTITY_TYPE_POST, dp.getId());
            int likeStatus = hostHolder.getUser()==null?
                    0:likeService.likeStatus(user.getId(), ENTITY_TYPE_POST, dp.getId());
            //点赞数量,点赞状态
            map.put("likeCount",likeCount);
            map.put("likeStatus",likeStatus);
            //回帖数量
            int commentCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, dp.getId());
            map.put("commentCount",commentCount);
            list.add(map);
        }
        model.addAttribute("discussPostList",list);
        if(hostHolder.getUser()!=null){
            User user = hostHolder.getUser();
            int unReadNoticeCount = messageService.findNoticeUnreadCount(null, user.getId());
            int unreadLetterCount = messageService.selectUnreadLetterCount(user.getId(), null);
            model.addAttribute("unReadCount",unReadNoticeCount+unreadLetterCount);
        }

        return "index";
    }

    @RequestMapping(value = "/addDiscussPost",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content){
        String json=discussPostService.addDiscussPost(title,content);
        return json;
    }

    @LoginRequired
    @RequestMapping(path = "/getDetail/{id}",method = RequestMethod.GET)
    public String getDetail(@PathVariable("id")String id,Model model,Page page){
        //帖子，作者
        DiscussPost discussPost=discussPostService.findDiscussPostById(id);
        User user = userService.getUserById(discussPost.getUserId());
        model.addAttribute("disscussPost",discussPost);
        model.addAttribute("users",user);
        long likeCount = likeService.likeCount(ENTITY_TYPE_POST, discussPost.getId());
        int likeStatus = hostHolder.getUser()==null?
                0:likeService.likeStatus(user.getId(), ENTITY_TYPE_POST, discussPost.getId());
        model.addAttribute("likeCount",likeCount);
        model.addAttribute("likeStatus",likeStatus);

        //评论，回复
        page.setLimit(5);
        page.setPath("/getDetail/"+id);
        page.setRows(discussPost.getCommentCount());
        List<Comment> commentList=commentService.findCommentsByEntity(ENTITY_TYPE_POST,id,page.getOffset(),page.getLimit());
        List<Map<String,Object>> voList=new ArrayList<>();
        for(Comment comment:commentList){
            HashMap<String, Object> mapComment = new HashMap<>();
            //当前评论user
            User user1 = userService.getUserById(comment.getUserId());
            //查这条评论的回复
            List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT, comment.getId() + "", 0, Integer.MAX_VALUE);
            List<Map<String,Object>> replyVoList=new ArrayList<>();
            if(replyList!=null) {
                for (Comment reply : replyList) {
                    HashMap<String, Object> mapReply = new HashMap<>();
                    //回复的人
                    User user2 = userService.getUserById(reply.getUserId());
                    mapReply.put("reply",reply);
                    mapReply.put("user",user2);
                    //回复的目标
                    User target=reply.getTargetId()==0?null:userService.getUserById(reply.getTargetId());
                    mapReply.put("target",target);

                    likeCount = likeService.likeCount(ENTITY_TYPE_COMMENT, reply.getId());
                    likeStatus = hostHolder.getUser()==null?
                            0:likeService.likeStatus(user.getId(), ENTITY_TYPE_COMMENT, reply.getId());
                    mapReply.put("likeCount",likeCount);
                    mapReply.put("likeStatus",likeStatus);
                    replyVoList.add(mapReply);
                }
            }
            //评论
            mapComment.put("comment",comment);
            //评论的人
            mapComment.put("user",user1);
            mapComment.put("rvl",replyVoList);
            //回复数量
            int replyCount=commentService.findCommentCount(ENTITY_TYPE_COMMENT,comment.getId());
            mapComment.put("replyCount",replyCount);

            likeCount = likeService.likeCount(ENTITY_TYPE_COMMENT, comment.getId());
            likeStatus = hostHolder.getUser()==null?
                    0:likeService.likeStatus(user.getId(), ENTITY_TYPE_COMMENT, comment.getId());
            mapComment.put("likeCount",likeCount);
            mapComment.put("likeStatus",likeStatus);
            voList.add(mapComment);
        }
        model.addAttribute("comments",voList);
        return "/site/discuss-detail";
    }
}
