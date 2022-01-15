package com.example.community.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;

@Configuration
public class WKConfig {
    private static final Logger logger= LoggerFactory.getLogger(WKConfig.class);

    @Value("${wk.storage}")
    private String wkStorage;

    @PostConstruct
    public void init(){
        File file=new File(wkStorage);
        if(!file.exists()){
            file.mkdir();
            logger.info("创建生成长图存放目录");
        }
    }
}
