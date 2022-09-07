package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void textTextMail(){
        mailClient.sendMail("2053414661@qq.com","Text","welcome");
    }

    @Test
    public void textHtmlMail(){
        Context context=new Context();
        context.setVariable("username","xvg");

        String content = templateEngine.process("/mail/demo",context);
        System.out.println(content);
        mailClient.sendMail("2053414661@qq.com","add","aed0073f431ac2ca");
    }
}
