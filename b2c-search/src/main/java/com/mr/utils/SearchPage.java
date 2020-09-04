package com.mr.utils;

import com.mr.common.utils.PageResult;
import com.mr.espojo.Goodes;
import com.mr.service.pojo.Brand;
import com.mr.service.pojo.Category;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class SearchPage extends PageResult<Goodes> {

    private List<Category> categoryList;

    private List<Brand> brandList;
    private List<Map<String, Object>> specMapList;

    public SearchPage(Long total, Long totalPage, List<Goodes> items, List<Category> categoryList, List<Brand> brandList, List<Map<String, Object>> specMapList) {
        super(total, totalPage, items);
        this.categoryList = categoryList;
        this.brandList = brandList;
        this.specMapList=specMapList;
    }

}
