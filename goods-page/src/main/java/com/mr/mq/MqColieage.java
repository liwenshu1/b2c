package com.mr.mq;

import com.mr.client.GoodsClient;
import com.mr.common.mqmessage.MessageRabbitmq;
import com.mr.common.utils.PageResult;
import com.mr.service.FileStaticService;
import com.mr.service.bo.SpuBo;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
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
    private FileStaticService fileStaticService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MessageRabbitmq.SPU_QUEUETOW_NAME, durable = "true"),
            exchange = @Exchange(
                    value = MessageRabbitmq.SPU_EXCHANGE_NAME,
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = {MessageRabbitmq.SPU_SAVE_MQ,MessageRabbitmq.SPU_UPDATE_MQ}))
    public void listen(String msg) throws Exception {
        Long spuId=Long.valueOf(msg);
        try {
            //更新静态文件
            fileStaticService.createStaticHtml(spuId);
        }catch (Exception e ){
                e.printStackTrace();
        }
        System.out.println(msg+"已接受");
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MessageRabbitmq.SPU_QUEUETOW_NAME, durable = "true"),
            exchange = @Exchange(
                    value = MessageRabbitmq.SPU_EXCHANGE_NAME,
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = {MessageRabbitmq.SPU_DELETE_MQ}))
    public void listenDeletes(String msg) throws Exception {
        Long spuId=Long.valueOf(msg);
        try {
            //更新静态文件
            fileStaticService.deleteStaticHtml(spuId);
        }catch (Exception e ){
            e.printStackTrace();
        }
        System.out.println(msg+"已接受");
    }
}
