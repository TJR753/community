package com.example.community;


import com.example.community.utils.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class MailClientTest {
    @Autowired
    private MailClient mailClient;

    @Test
    public void testMailClient(){
        mailClient.sendHtmlMessage("1819324794@qq.com","hello","hello");
    }

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testHtmlMailClient(){
        Context context = new Context();
        context.setVariable("to","1819324794@qq.com");
        String content = templateEngine.process("/demo/mail", context);
        mailClient.sendHtmlMessage("1819324794@qq.com","hello",content);
    }
}
