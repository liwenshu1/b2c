package com.mr.service;

import com.mr.api.UserClient;
import com.mr.bo.UserInfo;
import com.mr.config.JwtConfig;
import com.mr.pojo.User;
import com.mr.util.JwUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserClient client;

    @Autowired
    private JwtConfig jwtConfig;

    public String login(String username,String password)  {
        //验证 账号 密码
        User user = client.query(username,password);
        System.out.println(user);
        if(user==null){
            return null;
        }
        try{
            //组装用户载荷对象 生成token
            UserInfo userInfo = new UserInfo(user.getId(),user.getUsername());
            String token = JwUtils.generateToken(userInfo, jwtConfig.getPrivateKey(), jwtConfig.getExpire());
            return token;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }



    }
}
