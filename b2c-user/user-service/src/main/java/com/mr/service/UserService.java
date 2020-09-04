package com.mr.service;

import com.mr.mapper.UserMapper;
import com.mr.pojo.User;
import com.mr.util.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserService {

        @Autowired
        private UserMapper mapper;


    /**
     * 根据type判断查询的类型 1用户名 2手机
     * 根据data查询
     * @param data
     * @param type
     * @return
     */
    public Boolean checkUser(String data,Integer type){
        Boolean state=true;

        User user = new User();
        user.setUsername(data);
        if(type==2){
            user.setPhone(data);
        }else if(type==1){
            user.setUsername(data);
        }
        int i = mapper.selectCount(user);
        if(i==0){
            state=true;
        }else if(i!=0){
            state=false;
        }
        return state;
    }


    /**
     * 注册user 密码加盐
     * @param username
     * @param password
     * @param phone
     */
    public void register(String username,String password,String phone){
        User user = new User();
        user.setUsername(username);

        //获取盐
        String salt = Md5Util.generateSalt();

        //加盐加密
        user.setPassword(Md5Util.md5Hex(password,salt));


        user.setPhone(phone);
        user.setSalt(salt);
        user.setCreated(new Date());

        //save注册
        mapper.insert(user);
    }


    /**
     * 根据用户名和密码来查询user
     * @param username
     * @param password
     * @return
     */
    public User query(String username,String password){
        User userSel = new User();

        //根据用户名查询出数据
        userSel.setUsername(username);
        User user = mapper.selectOne(userSel);

        //如果user不为空
        if(user!=null){
            //校验密码 正确则返回 用户数据
            if(user.getPassword().equals(Md5Util.md5Hex(password,user.getSalt()))){
                return user;
            }else{
                return null;
            }
        }else{
            return null;
        }

    }
}
