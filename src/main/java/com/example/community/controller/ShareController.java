package com.example.community.controller;

import com.example.community.domain.Event;
import com.example.community.event.EventProducer;
import com.example.community.utils.CommunityConstant;
import com.example.community.utils.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;

@Controller
public class ShareController implements CommunityConstant {
    private static final Logger logger= LoggerFactory.getLogger(ShareController.class);

    @Value("${wk.command}")
    private String wkCommand;
    @Value("${wk.storage}")
    private String wkStorage;
    @Value("${wk.suffix}")
    private String wkSuffix;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/share",method = RequestMethod.GET)
    @ResponseBody
    public String share(String htmlUrl){
        String filename= CommunityUtil.generateUUID();

        Event event = new Event().setTopic(TOPIC_SHARE)
                .setData("filename",filename)
                .setData("storage",wkStorage)
                .setData("suffix",wkSuffix)
                .setData("command",wkCommand)
                .setData("htmlUrl",htmlUrl);
        eventProducer.fireEvent(event);

        //返回访问路径，domain+contextPath+/share/image/文件名
        HashMap<String, Object> map = new HashMap<>();
        map.put("shareUrl",domain+contextPath+"/share/image/"+filename+wkSuffix);
        return CommunityUtil.parseJson("0",null,map);
    }

    @RequestMapping(path = "/share/image/{filename}",method = RequestMethod.GET)
    public void getShareImage(@PathVariable("filename")String filename, HttpServletResponse response){
        if(filename==null){
            throw new IllegalArgumentException("文件名不能为空");
        }
        File file = new File(wkStorage + "/" + filename);
        response.setContentType("image/png;charset=utf-8");

        try(
                OutputStream os = response.getOutputStream();
                FileInputStream fis = new FileInputStream(file)
        ){
            byte[] buffer = new byte[1024];
            int b=0;
            while((b=fis.read(buffer))!=-1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("获取长图失败"+e.getMessage());
        }
    }
}
