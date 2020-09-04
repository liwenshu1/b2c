package com.mr.web;

import com.mr.common.enums.ExceptionEnum;
import com.mr.common.mrexception.MrException;
import com.mr.service.CategoryService;
import com.mr.service.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService service;

    @GetMapping("list")
    public ResponseEntity<List<Category>> list(
            @RequestParam(value = "pid",defaultValue = "0") Long pid){
        List<Category> list = service.queryCategory(pid);
        if (list==null && list.size()<1){
                throw new MrException(ExceptionEnum.CATEGORY_IS_NULL);
            }
        return ResponseEntity.ok(list);
    }

    @PostMapping("save")
    public ResponseEntity<String> save(
            @RequestBody Category category){
        if(category==null){
            throw new MrException(ExceptionEnum.CATEGORY_IS_NULL);
        }
        service.saveCategory(category);
        return ResponseEntity.ok("增加成功");
    }

    @PutMapping("update")
    public ResponseEntity<String> update(
            @RequestBody Category category){
        if(category==null){
            throw new MrException(ExceptionEnum.CATEGORY_IS_NULL);
        }
        service.updateCategory(category);
        return ResponseEntity.ok("修改成功");
    }

    @DeleteMapping("delete")
    public ResponseEntity delete(
            @RequestParam(value = "id") Long id){
        System.out.println(id);
        if (id==null){
            throw new MrException(ExceptionEnum.CATEGORY_IS_NULL);
        }
        service.deleteCategory(id);
        return ResponseEntity.ok(id);
    }

    @GetMapping("bid/{bid}")
    public ResponseEntity<List<Category>> find(@PathVariable("bid")Long bid){
        List<Category> list = this.service.queryByBrandId(bid);
        if (list==null ){
            throw new MrException(ExceptionEnum.CATEGORY_IS_NULL);
        }
        return ResponseEntity.ok(list);
    }
    /**
     * 根据商品分类id集合查询分类名称
     * @param ids id集合
     * @return 名称集合
     */
    @GetMapping("cateNames")
    public  ResponseEntity<List<Category>> queryCateNameByIds(@RequestParam("ids") List<Long> ids){
        List<Category> nameList= service.queryCateNameByIds(ids);
        return ResponseEntity.ok(nameList);
    }

}
