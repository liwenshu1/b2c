package com.mr.mapper;

import com.mr.service.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper extends  tk.mybatis.mapper.common.Mapper<User>{
}
