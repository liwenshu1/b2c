package com.mr.web;

import com.mr.bo.UserInfo;
import com.mr.common.utils.CookieUtils;
import com.mr.config.JwtConfig;
import com.mr.pojo.User;
import com.mr.service.AuthService;
import com.mr.util.JwUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class AuthController {

    @Autowired
    private AuthService service;

    @Autowired
    private JwtConfig jwtConfig;

    @PostMapping("login")
    public ResponseEntity<Void> login(
            @RequestParam(value = "username") String username,
            @RequestParam(value ="password") String password,
            HttpServletRequest request,
            HttpServletResponse response
    ){

        //校验账号和密码
        String token = service.login(username, password);
        //不正确就返回401 无权限访问
        System.out.println(token);
        if(token.isEmpty()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        //如果token有值就放入cookie中返回
        CookieUtils.setCookie(request,response,jwtConfig.getCookieName(),token,jwtConfig.getCookieMaxAge(),true);



        return  ResponseEntity.ok(null);
    }

    @GetMapping("verify")
    public ResponseEntity verify(
            @CookieValue("B2C_TOKEN") String token,
            HttpServletResponse response,
            HttpServletRequest request
    ){

        try{
            // 解析token
            UserInfo user = JwUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            //生成新token
            String newToken = JwUtils.generateToken(user, jwtConfig.getPrivateKey(),jwtConfig.getExpire());

            //将新生成的token返回
            CookieUtils.setCookie(request,response,jwtConfig.getCookieName(),newToken,jwtConfig.getCookieMaxAge(),true);
            return ResponseEntity.ok(user);
        }catch (Exception e){
            e.printStackTrace();
        }




        return null;
    }
}
