package com.mr.web;

import com.mr.common.utils.PageResult;
import com.mr.espojo.Goodes;
import com.mr.espojo.PageSearch;
import com.mr.service.GoodsIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("page")
public class SearchController {


    @Autowired
    private GoodsIndexService goodsIndexService;

    @PostMapping
    public ResponseEntity<PageResult<Goodes>> getGoodes(
            @RequestBody PageSearch pageSearch
            ){
        return  ResponseEntity.ok(goodsIndexService.getGoodEsForEs(pageSearch));
    }
}
