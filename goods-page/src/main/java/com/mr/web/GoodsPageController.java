package com.mr.web;

import com.mr.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("item")
public class GoodsPageController {

    @Autowired
    private ItemService goodsPageService;


    @RequestMapping("{id}.html")
    public String getGoodsMatch(@PathVariable("id") Long id, ModelMap map){

        Map<String, Object> goodsMatch = goodsPageService.getGoodsMatch(id);
        map.putAll(goodsMatch);

        return "item";
    }
}
