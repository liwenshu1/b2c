package com.mr.service.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "tb_spu_detail")
public class SpuDetil {

        @Id
        private Long    spuId;
        private String  description;
        private String  genericSpec;
        private String  specialSpec;
        private String  packingList;
        private String  afterService;

}
