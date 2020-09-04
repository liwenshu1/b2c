package com.mr.common.mqmessage;

public interface MessageRabbitmq {
    //spu  routting key
      String SPU_SAVE_MQ="spu.save";
      String SPU_UPDATE_MQ="spu.update";
      String SPU_DELETE_MQ="spu.delete";


    //spu Exchange name
      String SPU_EXCHANGE_NAME="topic-item";

     //spu Queue name
     String SPU_QUEUE_NAME="topic-search-item";
     String SPU_QUEUETOW_NAME="topic-search-item2";

}
