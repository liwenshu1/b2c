package com.mr.service.api;

import com.mr.common.utils.PageResult;
import com.mr.service.bo.SpuBo;
import com.mr.service.pojo.Goods;
import com.mr.service.pojo.Sku;
import com.mr.service.pojo.SpuDetil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("goods")
public interface GoodsApi {
        /**
         * 商品分页
         * @return
         */
        @GetMapping("list")
        public PageResult<SpuBo> lists(
                @RequestParam(value = "page",defaultValue = "1") Integer page,
                @RequestParam(value = "rows",defaultValue = "5") Integer rows,
                @RequestParam(value = "key") String key,
                @RequestParam(value = "saleable",defaultValue = "true") Boolean saleable
        );

        /**
         * 修改商品
         * @return
         */
        @PutMapping
        public void updateTheGoods(@RequestBody SpuBo spu);


        @GetMapping("spu/detail/{spuId}")
        public  SpuDetil getSpuDetails(@PathVariable("spuId")Long spuId);

        //获得sku集合
        @GetMapping("sku/{spuId}")
        public  List<Sku> getSkuBySpuId(@PathVariable("spuId") Long spuId);


        //查询 spu
        @GetMapping("querySpuById")
        public Goods getSpuBySpuId(@RequestParam("spuId") Long spuId);

        //根据skuId查询 sku具体参数
        @GetMapping("querySkuById")
        public Sku getSku(@RequestParam("skuId") Long skuId);
}
