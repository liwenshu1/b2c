package com.test;

import com.mr.SearchApplication;
import com.mr.client.BrandClient;
import com.mr.client.GoodsClient;
import com.mr.common.utils.PageResult;
import com.mr.dao.GoodsRepository;
import com.mr.espojo.Goodes;
import com.mr.service.GoodsIndexService;
import com.mr.service.bo.SpuBo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SearchApplication.class})
public class Tests {

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private GoodsIndexService  goodsIndexService;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private AmqpTemplate amqpTemplate;



    @Test
    public void testGoodsClient(){
        int page=0;
        Boolean load=true;
        int count=0;
        while(load){
        //es商品集合
        List<Goodes> goodesList =new ArrayList<>();


        PageResult<SpuBo> pageResult= goodsClient.lists(page++,10,"",true);
        pageResult.getItems().forEach(spuBo -> {
            //System.out.println(spuBo.getTitle()+"   "+spuBo.getBrandName());
            //将值付给goodslist集合 等待批量增加
            goodesList.add(goodsIndexService.buildGoodsBySpu(spuBo));
        });

            goodsRepository.saveAll(goodesList);
        //最后一页结束循环
            if(pageResult.getItems().size()<10){
                load=false;
                count+=pageResult.getItems().size();
            }else {
                    count+= goodesList.size();
                    System.out.println(page);

            }
        }
        //System.out.println(count+"数据");

    }


    @Test
    public void testMq(){
        //指定交换机 routting 和message
        amqpTemplate.convertAndSend("topic-search","item.save","153");
    }

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void createGoodsIndex(){

        elasticsearchTemplate.createIndex(Goodes.class);
        elasticsearchTemplate.putMapping(Goodes.class);
    }
}
