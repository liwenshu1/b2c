package com.mr.service.api;

import com.mr.common.enums.ExceptionEnum;
import com.mr.common.mrexception.MrException;
import com.mr.common.utils.PageResult;
import com.mr.service.pojo.Brand;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("brand")
public interface BrandApi {
    @GetMapping("queryBrandById")
    public Brand queryBrandById(@RequestParam("id") Long id);

    @GetMapping("list")
    public PageResult<Brand> list(
            @RequestParam(value = "key" , required = false) String key,
            @RequestParam(value = "page", defaultValue = "1") Integer  page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy",required = false) String sortBy,
            @RequestParam(value = "desc",required = false) Boolean desc
    );


    @PostMapping("/{cateIds}")
    public void save(
            Brand brand,
            @PathVariable("cateIds") List<Long> cateIds);
    @DeleteMapping()
    public void delete(
            @RequestParam("id") Long id);

    @PutMapping("/{cateIds}")
    public void update( @RequestBody Brand brand, @PathVariable("cateIds") List<Long> cateIds);

    @GetMapping("cid/{cid}")
    public List<Brand> lists(@PathVariable("cid") Long cid);

    @GetMapping("queryBrandByIds")
    public  List<Brand> queryBrandByIds(@RequestParam("ids") List<Long> ids);

}
