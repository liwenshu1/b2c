package com.mr.web;

import com.mr.common.enums.ExceptionEnum;
import com.mr.common.mrexception.MrException;
import com.mr.common.utils.PageResult;
import com.mr.service.MemberService;
import com.mr.service.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("member")
public class MemberController {

    @Autowired
    private MemberService service;
//    key:
//    page: 1
//    rows: 5
//    sortBy: id
//    desc: false
    @GetMapping("list")
    public ResponseEntity<PageResult> list(
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value ="rows",defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy",required = false) String sortBy,
            @RequestParam(value = "desc",required = false) Boolean desc
                                           ){
        PageResult<User> pageResult = service.select(page,rows,key,sortBy,desc);
        if(pageResult==null){
            throw new MrException(ExceptionEnum.MEMBER_IS_NULL);
        }
        return  ResponseEntity.ok(pageResult);
    }

    @PostMapping
    public ResponseEntity<String> save(@RequestBody User user){
        return  ResponseEntity.ok("增加完毕");
    }
}