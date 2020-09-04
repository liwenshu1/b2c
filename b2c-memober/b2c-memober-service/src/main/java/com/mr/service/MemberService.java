package com.mr.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mr.common.utils.PageResult;
import com.mr.mapper.MemberMapper;
import com.mr.service.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

@Service
public class MemberService {

    @Autowired
    private MemberMapper mapper;


    public PageResult<User> select(Integer page, Integer rows, String key, String sortBy, Boolean desc){
            //设置分页条数
        PageHelper.startPage(page,rows);
        //过滤条件
        Example example = new Example(User.class);

        //当key不为空时候添加模糊查询
        if(key!=null && !key.equals("")){
            System.out.println(111);
            example.createCriteria().andLike("username","%"+key.trim()+"%");
        }

        //sortBy不为空时候进行 排序 并且以desc的
        if(sortBy!=null&& !sortBy.equals("")&&desc!=null){
            example.setOrderByClause(sortBy+(desc? " asc":" desc"));
        }
            //使用分页助手提供的类接受 值
        Page<User> pageInfo = (Page<User>) mapper.selectByExample(example);

        return new PageResult<User>(pageInfo.getTotal(),pageInfo.getResult());
    }

    public Integer save (User user){
        int i = mapper.insertSelective(user);
        return i;
    }

    public Integer update(User user){

        return mapper.updateByPrimaryKeySelective(user);
    }


    public Integer delete(Long id){

        return mapper.deleteByPrimaryKey(id);
    }
}
