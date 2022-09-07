package com.nowcoder.community.service;

import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id){
        return userMapper.selectById(id);
    }

    public Map<String,Object> register(User user){
        Map<String,Object> map=new HashMap<>();
        //对空值判断处理
        if(user==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        if (StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }

        /**
         * 验证账号是否可用
         * 1.数据库里是否已经存在(只实现了这个)
         * 2.是否符合创建要求
         */
        User u=userMapper.selectByName(user.getUsername());
        if (u!=null){
            map.put("usernameMsg","账号已存在");
            return map;
        }
        //验证邮箱
        u=userMapper.selectByEmail(user.getEmail());
        if (u!=null){
            map.put("emailMsg","邮箱已被注册");
            return map;
        }

        //注册用户，把信息存入库中
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));//随机生成0-4位字符串，加到密码上
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));//对处理过后的密码进行加密
        user.setType(0);//默认Type状态
        user.setStatus(0);//默认Status状态
        user.setActivationCode(CommunityUtil.generateUUID());//随机生成激活字符串
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));//随机生成头像（牛客网云服务端的一些头像）
        user.setCreateTime(new Date());//创建的时间
        userMapper.insertUser(user);//导入库中

        //发生激活邮件
        Context context=new Context();
        context.setVariable("email",user.getEmail());
        //发送激活链接url:  http://localhost:8088/community/activation/101/code
        String url=domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();//在配置文件中配置了自动生成id，第22行
        context.setVariable("url",url);
        String content=templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);

        return map;
    }
    public int activation(int userId,String code){
        User user=userMapper.selectById(userId);
        if (user.getStatus()==1){
            return ACTIVATION_REPEAT;
        }else if (user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FAILURE;
        }
    }

}
