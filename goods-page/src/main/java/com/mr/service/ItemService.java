package com.mr.service;

import com.mr.client.BrandClient;
import com.mr.client.CategoryClient;
import com.mr.client.GoodsClient;
import com.mr.client.SpecClient;
import com.mr.service.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemService {


    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private SpecClient specClient;

    public Map<String,Object> getGoodsMatch(Long spuId){
        //需要数据 spu spuDetail sku category brand specGroup specParam

        //查询spu
        Goods spu = goodsClient.getSpuBySpuId(spuId);
        //查询spuDetail
        SpuDetil spuDetil = goodsClient.getSpuDetails(spuId);
        //查询sku
        List<Sku> skuList = goodsClient.getSkuBySpuId(spuId);

        //查询三级分类
        List<Category> categoryList = categoryClient.queryCateNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        //查询brand
        Brand brand = brandClient.queryBrandById(spu.getBrandId());

        //查询 specGroup
        List<Spec> specGroupList = specClient.groups(spu.getCid3());
        //规格组填充规格参数
        specGroupList.forEach(spec -> {
            List<SpecParam> paramList = specClient.params(spec.getId(), null, null, null);
            spec.setSpecParamList(paramList);
        });

        //查询 规格参数特有属性
        List<SpecParam> specParamList = specClient.params(null, spu.getCid3(), null, false);
        //处理特有属性(因为我们前台只需要其id 和 name)
        Map<Long,Object> specParamMap=new HashMap<>();
        specParamList.forEach(specParam -> {
            specParamMap.put(specParam.getId(),specParam.getName());
        });

        //拼接属性
        Map<String,Object> modelMap =new HashMap<>();
        modelMap.put("spu",spu);
        modelMap.put("spuDetil",spuDetil);
        modelMap.put("skuList",skuList);
        modelMap.put("categoryList",categoryList);
        modelMap.put("brand",brand);
        modelMap.put("specGroupList",specGroupList);
        modelMap.put("specParamList",specParamMap);

        return modelMap;
    }
}
