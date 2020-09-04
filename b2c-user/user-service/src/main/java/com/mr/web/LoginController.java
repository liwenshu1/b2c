package com.mr.web;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class LoginController {


    @GetMapping("login")
    public String login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session){

        if(username.equals("zhangsan")&&password.equals("123123")){
            session.setAttribute("user",username);
            return "login ok";
        }


        return  "no";
    }

    @GetMapping("pease")
    public String pease(HttpSession session){

        System.out.println("session id:"+session.getId());
        System.out.println(session.getAttribute("user"));

        return "login:"+session.getAttribute("user")+" ok";
    }
}
