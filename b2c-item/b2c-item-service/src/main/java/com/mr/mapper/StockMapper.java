package com.mr.mapper;

import com.mr.service.pojo.Stock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;

@Mapper
public interface StockMapper extends  tk.mybatis.mapper.common.Mapper<Stock>, DeleteByIdListMapper<Stock,Long> {

    @Select("SELECT * FROM tb_stock WHERE sku_id =#{skuId}")
    Stock selectBySkuId(@Param("skuId") Long skuId);
}
