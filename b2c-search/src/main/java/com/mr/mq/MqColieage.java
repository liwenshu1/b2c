package com.mr.mq;

import com.mr.client.GoodsClient;
import com.mr.common.mqmessage.MessageRabbitmq;
import com.mr.dao.GoodsRepository;
import com.mr.espojo.Goodes;
import com.mr.service.GoodsIndexService;
import com.mr.service.pojo.Goods;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component
public class MqColieage {

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private GoodsIndexService goodsIndexService;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MessageRabbitmq.SPU_QUEUE_NAME, durable = "true"),
            exchange = @Exchange(
                    value = MessageRabbitmq.SPU_EXCHANGE_NAME,
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = {MessageRabbitmq.SPU_SAVE_MQ,MessageRabbitmq.SPU_UPDATE_MQ}))
    public void listenSaveOrUpdate(String msg){
        try{
            this.changeData(msg);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("消费者接收到消息：" + msg);
    }




    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MessageRabbitmq.SPU_QUEUE_NAME, durable = "true"),
            exchange = @Exchange(
                    value = MessageRabbitmq.SPU_EXCHANGE_NAME,
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = {MessageRabbitmq.SPU_DELETE_MQ}))
    public void listenDelete(String msg){

            Long spuId=Long.valueOf(msg);
            try{
                goodsRepository.deleteById(spuId);
            }catch (Exception e){
                e.printStackTrace();
            }

            System.out.println("消费者接收到消息：" + msg);


    }


    private void changeData(String msg){
        Long spuId=Long.valueOf(msg);
        Goods spu = goodsClient.getSpuBySpuId(spuId);
        goodsRepository.save(goodsIndexService.buildGoodsBySpu(spu));
    }



}
