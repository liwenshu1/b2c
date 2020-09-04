package com.mr.api;

import com.mr.pojo.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping
public interface UserApi {



    /**
     * 根据手机/用户名来验证帐号是否存在
     * @param data
     * @param type
     * @return
     */
    @RequestMapping("/check/{data}/{type}")
    public ResponseEntity checkUser(
            @PathVariable(value ="data") String  data,
            @PathVariable(value ="type") Integer type
    );

    /**
     * 注册user
     * @param username
     * @param password
     * @param phone
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity  register(
            @RequestParam(value = "username") String username,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "phone") String phone
    );

    @GetMapping("/query")
    public User  query(
            @RequestParam(value = "username") String username,
            @RequestParam(value = "password") String password
    );
}
