package com.mr.service.api;

import com.mr.common.enums.ExceptionEnum;
import com.mr.common.mrexception.MrException;
import com.mr.service.pojo.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RequestMapping("category")
public interface CategoryApi {

    /**
     * 查询分类名称集合
     *
     * @param ids
     * @return
     */
    @GetMapping("cateNames")
    public List<Category> queryCateNameByIds(@RequestParam("ids") List<Long> ids);

    @GetMapping("list")
    public List<Category> list(
            @RequestParam(value = "pid", defaultValue = "0") Long pid);

    @PostMapping("save")
    public void save(
            @RequestBody Category category);

    @PutMapping("update")
    public void update(
            @RequestBody Category category);

    @DeleteMapping("delete")
    public Category delete(
            @RequestParam(value = "id") Long id);

    @GetMapping("bid/{bid}")
    public List<Category> find(@PathVariable("bid") Long bid);
}