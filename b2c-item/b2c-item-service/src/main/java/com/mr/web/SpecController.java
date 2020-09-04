package com.mr.web;

import com.mr.common.enums.ExceptionEnum;
import com.mr.common.mrexception.MrException;
import com.mr.service.SpecService;
import com.mr.service.pojo.Spec;
import com.mr.service.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("spec")
//http://api.b2c.com/api/item/spec/groups/76 //查询规格
//http://api.b2c.com/api/item/spec/params?gid=2 //查询 规格详细数据
//http://api.b2c.com/api/item/spec/group 新增规格主体
//http://api.b2c.com/api/item/spec/param 新增 详细参数
//http://api.b2c.com/api/item/spec/group/1 删除规格
public class SpecController {


    @Autowired
    private SpecService service;

//    规格查询 group
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<Spec>> groups(@PathVariable("cid") Long cid){
        List<Spec> list = service.list(cid);
        if(list==null && list.size()<1){
            throw new MrException(ExceptionEnum.CATEGORY_IS_NULL);
        }
        return ResponseEntity.ok(list);
    }
//规格具体参数查询 param
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> params(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "searching",required = false) Boolean searching,
            @RequestParam(value = "generic",required = false) Boolean generic){
        List<SpecParam> list = service.querySpecByCid(cid,gid,searching,generic);
        if(list==null && list.size()<1){
            throw new MrException(ExceptionEnum.CATEGORY_IS_NULL);
        }
        return ResponseEntity.ok(list);
    }
//    @GetMapping("/{cid}")
//    public ResponseEntity<List<SpecParam>> querySpecByCid(@PathVariable("cid") Long cid){
//        List<SpecParam> list = service.querySpecByCid(cid);
//        if(list==null && list.size()<1){
//            throw new MrException(ExceptionEnum.CATEGORY_IS_NULL);
//        }
//        return ResponseEntity.ok(list);
//    }

    @PostMapping("group")
    public ResponseEntity<String> save(@RequestBody Spec spec){

        if(spec==null ){
            throw new MrException(ExceptionEnum.USER_IS_NULL);
        }
        int i = service.save(spec);
        return ResponseEntity.ok("新增成功");
    }

    @PostMapping("param")
    public ResponseEntity<String> saveParam(@RequestBody SpecParam specParam){

        if(specParam==null ){
            throw new MrException(ExceptionEnum.USER_IS_NULL);
        }
        int i = service.saveParam(specParam);
        return ResponseEntity.ok("新增成功");
    }

    @DeleteMapping("group/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id){

        if(id==null ){
            throw new MrException(ExceptionEnum.PRICE_CANNOT_BE_NULL);
        }
        int i = service.delete(id);
        return ResponseEntity.ok("删除成功");
    }
    @DeleteMapping("param/{id}")
    public ResponseEntity<String> deleteParam(@PathVariable("id") Long id){

        if(id==null ){
            throw new MrException(ExceptionEnum.PRICE_CANNOT_BE_NULL);
        }
        int i = service.deleteParam(id);
        return ResponseEntity.ok("删除成功");
    }

    @PutMapping("group")
    public ResponseEntity<String> put(@RequestBody Spec spec){

        if(spec==null ){
            throw new MrException(ExceptionEnum.PRICE_CANNOT_BE_NULL);
        }
        int i = service.put(spec);
        return ResponseEntity.ok("删除成功");
    }

    @PutMapping("param")
    public ResponseEntity<String> putParam(@RequestBody SpecParam specParam){

        if(specParam==null ){
            throw new MrException(ExceptionEnum.PRICE_CANNOT_BE_NULL);
        }
        int i = service.putParam(specParam);
        return ResponseEntity.ok("删除成功");
    }

}
