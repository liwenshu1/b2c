package com.mr.web;

import com.mr.pojo.User;
import com.mr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class UserController {

    @Autowired
    private UserService service;


    /**
     * 根据手机/用户名来验证帐号是否存在
     * @param data
     * @param type
     * @return
     */
    @RequestMapping("/check/{data}/{type}")
    public ResponseEntity  checkUser(
            @PathVariable(value ="data") String  data,
            @PathVariable(value ="type") Integer type
    ){
        if(data==null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        Boolean statues=null;
        try{
            statues = service.checkUser(data, type);
        }catch (Exception e){
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return  ResponseEntity.ok(statues);
    }

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
    ){
        if(username==null||password==null||phone==null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        try{
            service.register( username, password,phone);
            return new  ResponseEntity(HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);

        }

    }

    @GetMapping("/query")
    public ResponseEntity<User>  query(
            @RequestParam(value = "username") String username,
            @RequestParam(value = "password") String password
    ){
        if(username==null||password==null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        try{
            User user = service.query(username, password);
            if(user!=null){
                return  ResponseEntity.ok(user);
            }else{
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e){
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);

        }

    }

}
