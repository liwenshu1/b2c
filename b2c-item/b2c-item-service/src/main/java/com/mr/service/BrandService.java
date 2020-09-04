package com.mr.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mr.common.utils.PageResult;
import com.mr.mapper.BrandMapper;
import com.mr.service.pojo.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {


    @Autowired
    private BrandMapper mapper;

    public PageResult<Brand> queryBrand(Integer page,Integer rows,String key,String sortBy, Boolean desc){
        //分页
        //设置当前页面条数
        PageHelper.startPage(page,rows);

        //过滤条件
        Example example = new Example(Brand.class);

        if (key !=null && !key.equals("")){
            //and 交集设置模糊查询
            example.createCriteria().andLike("name","%"+key.trim()+"%");
        }
        //排序
        if(sortBy !=null && !sortBy.equals("")){
            example.setOrderByClause(sortBy+(desc? " asc" : " desc"));
        }
        //使用分页助手提供的类接受 值
      Page<Brand> pageInfo= (Page<Brand>) mapper.selectByExample(example);

        return new PageResult<Brand>(pageInfo.getTotal(),pageInfo.getResult());
    }

    @Transactional
    public void saveBrand(Brand brand, List<Long> cateIds){
       //新增的品牌信息
        mapper.insert(brand);

        mapper.deleteCategoryBrand(brand.getId());
        //新增的品牌和分类信息
        for (Long cid:
             cateIds) {
            mapper.insertCategoryBrand(cid,brand.getId());
        }
    }

    public void delete(Long id){
        mapper.deleteByPrimaryKey(id);
    }

    public void update(Brand brand,List<Long> cateIds){
        mapper.updateByPrimaryKeySelective(brand);
        mapper.deleteCategoryBrand(brand.getId());

        //新增的品牌和分类信息
        for (Long cid:
                cateIds) {
            mapper.insertCategoryBrand(cid,brand.getId());
        }
    }

    public List<Brand> lists(Long cid){

        return mapper.queryBrands(cid);
    }


    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    public Brand queryBrandById(Long id){
        //查询品牌
        Brand brand=mapper.selectByPrimaryKey(id);

        return  brand;
    }

    /**
     * 根据id查询品牌
     * @param ids
     * @return
     */
    public List<Brand> queryBrandByIds(List<Long> ids){
        //查询品牌
        List<Brand> list = mapper.selectByIdList(ids);

        return  list;
    }

}
