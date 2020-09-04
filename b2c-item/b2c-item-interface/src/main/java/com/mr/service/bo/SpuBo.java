package com.mr.service.bo;

import com.mr.service.pojo.Goods;

import com.mr.service.pojo.Sku;
import com.mr.service.pojo.SpuDetil;
import lombok.Data;

import java.util.List;

@Data
public class SpuBo extends Goods {

    private   String categoryName;

    private  String brandName;

    private SpuDetil spuDetail;

    private List<Sku> skus;
}
