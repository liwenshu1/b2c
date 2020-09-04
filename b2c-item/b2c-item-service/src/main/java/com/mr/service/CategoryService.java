package com.mr.service;

import com.mr.mapper.CategoryMapper;
import com.mr.service.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper mapper;

    public List<Category> queryCategory(Long pid){
        Category category = new Category();
        category.setParentId(pid);
        return mapper.select(category);
    }

    public void deleteCategory(Long id){
        mapper.deleteByPrimaryKey(id);
    }

    public void saveCategory(Category category){
        mapper.insert(category);
    }

    public void updateCategory(Category category){
        mapper.updateByPrimaryKeySelective(category);
    }
    public List<Category> queryByBrandId(Long bid) {
        return mapper.queryByBrandId(bid);
    }

    /**
     * 根据id查询多个分类名称
     * @param ids
     * @return
     */
    public List<Category> queryCateNameByIds(List<Long> ids){
        //查询多条数据
        List<Category> list=mapper.selectByIdList(ids);

        return  list;
    }
}
