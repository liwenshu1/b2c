package com.mr.mapper;

import com.mr.service.pojo.Brand;
import org.apache.ibatis.annotations.*;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.ids.SelectByIdsMapper;

import java.util.List;

@Mapper
public interface BrandMapper extends tk.mybatis.mapper.common.Mapper<Brand> , SelectByIdListMapper<Brand,Long> {

    @Insert("INSERT INTO tb_category_brand (category_id,brand_id) VALUES(#{cid},#{bid})")
    int insertCategoryBrand(@Param("cid") Long cid,@Param("bid") Long bid);

    @Delete("DELETE FROM tb_category_brand WHERE brand_id=#{bid}")
    int deleteCategoryBrand(@Param("bid") Long bid);

    @Select("SELECT * FROM tb_brand WHERE id IN(SELECT brand_id FROM tb_category_brand WHERE category_id = #{cid} )")
    List<Brand> queryBrands(@Param("cid")Long cid);

    @Select("SELECT * FROM tb_brand WHERE id=#{bid}")
    Brand queryBrandsById(@Param("bid") Long bid);

}
