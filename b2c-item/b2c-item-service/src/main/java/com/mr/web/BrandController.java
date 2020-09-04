package com.mr.web;

import com.mr.common.enums.ExceptionEnum;
import com.mr.common.mrexception.MrException;
import com.mr.common.utils.PageResult;
import com.mr.service.BrandService;
import com.mr.service.pojo.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("brand")
public class BrandController {

    @Autowired
    private BrandService service;
    @GetMapping("list")
    public ResponseEntity<PageResult> list(
            @RequestParam(value = "key" , required = false) String key,
            @RequestParam(value = "page", defaultValue = "1") Integer  page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy",required = false) String sortBy,
            @RequestParam(value = "desc",required = false) Boolean desc
            ){
        PageResult<Brand> pageResult = service.queryBrand(page, rows, key, sortBy, desc);
        if (pageResult==null){

            throw new MrException(ExceptionEnum.CATEGORY_IS_NULL);
        }
        return ResponseEntity.ok(pageResult);
    }


    @PostMapping("/{cateIds}")
    public ResponseEntity<Void> save(
             Brand brand,
             @PathVariable("cateIds") List<Long> cateIds){
        if(brand ==null){
            throw new MrException(ExceptionEnum.CATEGORY_IS_NULL);
        }
        service.saveBrand(brand,cateIds);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
   @DeleteMapping()
    public ResponseEntity delete(
            @RequestParam("id") Long id){
        if(id ==null){
            throw new MrException(ExceptionEnum.CATEGORY_IS_NULL);
        }
        service.delete(id);
        return ResponseEntity.ok(1);
    }

   @PutMapping("/{cateIds}")
    public ResponseEntity update( @RequestBody Brand brand, @PathVariable("cateIds") List<Long> cateIds){
        if(brand ==null){
            throw new MrException(ExceptionEnum.CATEGORY_IS_NULL);
        }
        service.update(brand,cateIds);
        return ResponseEntity.ok(1);
    }

    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> lists(@PathVariable("cid") Long cid){

        List<Brand> lists = service.lists(cid);
        if(lists==null){
            throw new MrException(ExceptionEnum.USER_IS_NULL);
        }
        return ResponseEntity.ok(lists);
    }

    /**
     * 根据品牌id集合查询品牌
     * @param  id
     * @return 品牌对象
     */
    @GetMapping("queryBrandById")
    public  ResponseEntity<Brand> queryBrandById(@RequestParam("id") Long id){
        Brand brand= service.queryBrandById(id);
        return ResponseEntity.ok(brand);
    }

    /**
     * 根据品牌id集合查询品牌
     * @param  ids
     * @return 品牌对象
     */
    @GetMapping("queryBrandByIds")
    public  ResponseEntity<List<Brand>> queryBrandByIds(@RequestParam("ids") List<Long> ids){
        List<Brand> list = service.queryBrandByIds(ids);
        return ResponseEntity.ok(list);
    }
}
