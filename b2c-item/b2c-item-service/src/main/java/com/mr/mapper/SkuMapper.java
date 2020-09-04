package com.mr.mapper;

import com.mr.service.pojo.Sku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SkuMapper extends  tk.mybatis.mapper.common.Mapper<Sku>{


    @Select("SELECT id FROM tb_sku WHERE spu_id =#{spuId} ")
    List<Sku> selectIdBySpu(@Param("spuId") Long spuId);


}
