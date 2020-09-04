package com.mr.mapper;

import com.mr.service.pojo.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.ids.SelectByIdsMapper;

import java.util.List;

@Mapper
public interface CategoryMapper extends tk.mybatis.mapper.common.Mapper<Category> ,SelectByIdListMapper<Category, Long>  {
    @Select("SELECT * FROM tb_category WHERE id IN (SELECT category_id FROM tb_category_brand WHERE brand_id = #{bid})")
    List<Category> queryByBrandId(@Param("bid") Long bid);

//    @Select("SELECT name FROM tb_category WHERE id IN (#{cid1},#{cid2}.#{cid3})")
//    List<Category> queryNameById(@Param("cid1")Long cid1,@Param("cid2")Long cid2,@Param("cid3")Long cid3);
}
