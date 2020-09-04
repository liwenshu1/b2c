package com.mr.web;

import com.mr.common.enums.ExceptionEnum;
import com.mr.common.mqmessage.MessageRabbitmq;
import com.mr.common.mrexception.MrException;
import com.mr.common.utils.PageResult;
import com.mr.service.GoodsService;
import com.mr.service.bo.SpuBo;
import com.mr.service.pojo.Goods;
import com.mr.service.pojo.Sku;
import com.mr.service.pojo.SpuDetil;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("goods")
public class GoodsController {

    @Autowired
    private GoodsService service;



    //ampq构造器 用来 创建交换机等
    @Autowired
    private AmqpTemplate amqpTemplate;
    //页面查询 Goods
    @GetMapping("list")
    public ResponseEntity<PageResult<SpuBo>> lists(
            @RequestParam(value = "page",defaultValue = "5") Integer page,
            @RequestParam(value = "rows",defaultValue = "1") Integer rows,
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "saleable",defaultValue = "false") Boolean saleable
            ){
        PageResult<SpuBo> list = service.list(page, rows, key, saleable);
        if(list==null){
            throw new MrException(ExceptionEnum.CATEGORY_IS_NULL);
        }
        return ResponseEntity.ok(list);

    }
    //新增 Goods
    @PostMapping
    public ResponseEntity<Void> save(@RequestBody SpuBo spu){
        try {
            service.save(spu);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //修改 Goods
    @PutMapping
    public ResponseEntity<Void> updateTheGoods(@RequestBody SpuBo spu){
        try {
            service.updateTheGoods(spu);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }
    //查询 SpuDetail
    @GetMapping("spu/detail/{spuId}")
    public ResponseEntity<SpuDetil> getSpuDetails(@PathVariable("spuId") Long spuId){

        SpuDetil detail = service.getSpuDetail(spuId);
        return  ResponseEntity.ok(detail);
    }

    //查询 Sku集合
    @GetMapping("sku/{spuId}")
    public ResponseEntity<List<Sku>> getSkuBySpuId(@PathVariable("spuId") Long spuId){

        List<Sku> skus = service.getSkuBySpuId(spuId);
        return  ResponseEntity.ok(skus);
    }

    //删除商品
    @DeleteMapping()
    public ResponseEntity<Void> deleteSpu(@RequestParam("id") Long id){

        service.deleteBySpuId(id);
        amqpTemplate.convertAndSend(MessageRabbitmq.SPU_EXCHANGE_NAME,MessageRabbitmq.SPU_DELETE_MQ,id);
        return null;
    }

    //更改商品 上下架 状态
    @PutMapping("upDowns")
    public ResponseEntity<Void> upDowns(@RequestBody SpuBo bo){
        service.updateSaleable(bo.getSaleable(),bo.getId());
        return ResponseEntity.ok(null);
    }


    //查询 spu
    @GetMapping("querySpuById")
    public Goods getSpuBySpuId(@RequestParam("spuId") Long spuId){


        return  service.getSpuBySpuId(spuId);
    }


    //根据skuId查询 sku具体参数
    @GetMapping("querySkuById")
    public Sku getSku(@RequestParam("skuId") Long skuId){

        return service.getSkuById(skuId);
    }

}
