package com.example.community.controller;

import com.example.community.annotation.LoginRequired;
import com.example.community.domain.Comment;
import com.example.community.domain.DiscussPost;
import com.example.community.domain.User;
import com.example.community.domain.vo.Page;
import com.example.community.service.*;
import com.example.community.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(path = "/user")
public class UserController implements CommunityConstant {
    private static final Logger logger= LoggerFactory.getLogger(UserController.class);

    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${community.path.upload}")
    private String uploadPath;

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private FollowService followService;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CommentService commentService;

    @LoginRequired
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }
    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String upload(MultipartFile headerImage, Model model){
        //??????
        if(headerImage==null){
            model.addAttribute("error","????????????????????????");
            return "/site/setting";
        }
        String originalFilename = headerImage.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        if(suffix==null){
            model.addAttribute("error","??????????????????");
            return "/site/setting";
        }
        //http://localhost:8080/community/static/user/{filename}
        //?????????????????????
        String filename= CommunityUtil.generateUUID()+suffix;
        String savePath=uploadPath +"/"+ filename;
        File file = new File(savePath);
        try {
            //??????????????????????????????
            headerImage.transferTo(file);
        } catch (IOException e) {
            logger.error("??????????????????"+e.getMessage());
            throw new RuntimeException("????????????????????????????????????",e);
        }
        //??????????????????
        User user = hostHolder.getUser();
        String headerUrl=domain+contextPath+"/user/header/"+filename;
        userService.updateHeaderUrl(user.getId(),headerUrl);
        return "redirect:/index";
    }
    @RequestMapping(path = "/header/{filename}",method = RequestMethod.GET)
    public void getImage(@PathVariable("filename")String filename, HttpServletResponse response){
        //?????????????????????
        String realPath=uploadPath+"/"+filename;
        String suffix = filename.substring(filename.lastIndexOf("."));
        response.setContentType("image/"+suffix);
        try(ServletOutputStream os = response.getOutputStream();
            FileInputStream fis = new FileInputStream(realPath)){
            byte[] buffer=new byte[1024];
            int b=0;
            while ((b=fis.read(buffer))!=-1){
                os.write(buffer,0,1024);
            }
        } catch (IOException e) {
            logger.error("??????????????????"+e.getMessage());
        }
    }
    @LoginRequired
    @RequestMapping(path = "/updatePassword",method = RequestMethod.POST)
    public String updatePassword(String password, String newPassword, String newSecondPassword,
                                 Model model, HttpServletRequest request, @CookieValue("ticket")String ticket){
        HashMap<String,Object> map=userService.setPassword(password,newPassword,newSecondPassword);
        if(map.containsKey("success")){
            //????????????
            userService.logout(ticket);
            return "redirect:/login";
        }
        else{
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("newPasswordMsg",map.get("newPasswordMsg"));
            model.addAttribute("newSecondPasswordMsg",map.get("newSecondPasswordMsg"));
            return "/site/setting";
        }
    }

    @RequestMapping(path = "/profile/{userId}",method = RequestMethod.GET)
    public String userDetail(@PathVariable("userId")int userId,Model model){
        User user = userService.getUserById(userId);
        if(user==null){
            throw new RuntimeException("??????????????????");
        }
        int userLikeCount = likeService.userLikeCount(userId);
        model.addAttribute("users",user);
        model.addAttribute("userLikeCount",userLikeCount);

        long followeeCount = followService.followeeCount(userId, ENTITY_TYPE_USER);
        long followerCount = followService.followerCount(userId, ENTITY_TYPE_USER);
        boolean hasFollowed=false;
        if(hostHolder.getUser()!=null){
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("followeeCount",followeeCount);
        model.addAttribute("followerCount",followerCount);
        model.addAttribute("hasFollowed",hasFollowed);
        return "/site/profile";
    }
    @RequestMapping(path = "/getMyPost/{userId}",method = RequestMethod.GET)
    public String getMyPost(Model model, Page page,@PathVariable("userId")Integer userId){
        page.setLimit(5);
        page.setPath("/getMyPost/"+userId);
        List<DiscussPost> postList = discussPostService.getMyPost(userId);
        model.addAttribute("postCount",postList.size());
        page.setRows(postList.size());
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for(DiscussPost dp:postList){
            HashMap<String, Object> map = new HashMap<>();
            String entityLikeKey = RedisKeyUtil.getEntityLikeKey(ENTITY_TYPE_POST, dp.getId());
            Long likeCount = redisTemplate.opsForSet().size(entityLikeKey);
            map.put("post",dp);
            map.put("likeCount",likeCount);
            list.add(map);
        }
        model.addAttribute("postList",list);
        model.addAttribute("users",userService.getUserById(userId));
        return "/site/my-post";
    }
    @RequestMapping(path = "/getMyReply/{userId}",method = RequestMethod.GET)
    public String getMyReply(Model model,Page page,@PathVariable("userId")Integer userId){
        page.setLimit(5);
        page.setPath("/getMyReply"+userId);
        List<Comment> myReply = commentService.getMyReply(userId);
        page.setRows(myReply.size());
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for(Comment c:myReply){
            HashMap<String, Object> map = new HashMap<>();
            DiscussPost discussPost = discussPostService.findDiscussPostById(c.getEntityId()+"");
            map.put("comment",c);
            map.put("post",discussPost);
            list.add(map);
        }
        model.addAttribute("myReply",list);
        model.addAttribute("myReplyCount",myReply.size());
        model.addAttribute("users",userService.getUserById(userId));
        return "/site/my-reply";
    }
}
