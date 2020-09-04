package com.mr.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mr.common.mqmessage.MessageRabbitmq;
import com.mr.common.utils.PageResult;
import com.mr.mapper.*;
import com.mr.service.bo.SpuBo;
import com.mr.service.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GoodsService {

    //ampq构造器 用来 创建交换机等
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private GoodsMapper mapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    /**
     * 分页查询
     * @param page
     * @param rows
     * @param key
     * @param saleable
     * @return
     */
    public PageResult<SpuBo> list(Integer page, Integer rows, String key, Boolean saleable) {
        //添加分页 这里我们是用的是分页工具
        PageHelper.startPage(page,rows);

        //实例化 example 对象去 做查询 有参构造为 查询的实体类
        Example example = new Example(Goods.class);

        //创建 example.createCriteria对象 用来 添加模糊查询 是否排序 等
        Example.Criteria criteria = example.createCriteria();

        //给予 key 模糊查询 条件是 ! null
        if(StringUtils.isNotEmpty(key)){
            criteria.andLike("title","%"+key+"%");
        }
        //添加 上下架的条件查询
        criteria.andEqualTo("saleable",saleable);
        //利用example 查询 出表中的数据 用分页工具来接受参数
        Page<Goods> pageInfo = (Page<Goods>) mapper.selectByExample(example);

        //回显商品类别 和 品牌
        List<SpuBo> list = pageInfo.stream().map(spu -> {
            //创建 用来接受 品牌 和 商品类别的 类 同时 继承 goods 表
            SpuBo spuBo = new SpuBo();
            //将查询出来的 goods 中的 值 拷贝到 spuBo中 以备 返回页面使用
            BeanUtils.copyProperties(spu,spuBo);

            //循环查询每个 spu id中的 商品类和 品牌
            //商品类
            //将 spu 里的 cid 取出并转换成list对象
            List<Long> list1 = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
            //调用 mapper 中的 selectByIdList方法 (此方法必须被 mapper继承并泛型 )
            List<Category> list2 = categoryMapper.selectByIdList(list1);
            //取出 List<Category> 中的 categoryNames
           List<String> list3  =list2.stream().map(categorys->  categorys.getName()).collect(Collectors.toList());
           //分割
            String CateNames = StringUtils.join(list3, "/");
            spuBo.setCategoryName(CateNames);

            //品牌
            Brand brand = brandMapper.queryBrandsById(spu.getBrandId());
            spuBo.setBrandName(brand.getName());

            return spuBo;
        }).collect(Collectors.toList());


        return new PageResult<SpuBo>(pageInfo.getTotal(),list);
    }


    /**
     *
     * insert
     * @param spu
     */
    @Transactional
    public void save(SpuBo spu) {
        //保存 spu
            spu.setSaleable(true);//是否上下架
            spu.setValid(true);//是否生效
            spu.setCreateTime(new Date());
            spu.setLastUpdateTime(new Date());
            mapper.insertSelective(spu);


        //保存 spuDetial
        SpuDetil spuDetail = spu.getSpuDetail(); 
        spuDetail.setSpuId(spu.getId());
        spuDetailMapper.insertSelective(spuDetail);

        // 调用insertinsertSkuAndStock
        List<Sku> skus = spu.getSkus();
        this.insertSkuAndStock(skus,spu.getId());

        amqpTemplate.convertAndSend(MessageRabbitmq.SPU_EXCHANGE_NAME,MessageRabbitmq.SPU_SAVE_MQ,spu.getId());

    }

    /**
     * 增加 sku 和 stock
     * @param skus
     * @param spuId
     */
    private void insertSkuAndStock(List<Sku> skus,Long spuId){
        // insert sku
        skus.forEach(sku->{
            //回流问题  //回调参数不可选 getEnable
            if(sku.getEnable()){
                //添加sku
                sku.setCreateTime(new Date());
                sku.setLastUpdateTime(new Date());
                sku.setSpuId(spuId);
                skuMapper.insertSelective(sku);

                //添加stock
                Stock stock = new Stock();
                stock.setSkuId(sku.getId());
                stock.setStock(sku.getStock());
                stockMapper.insert(stock);
            }
        });
    }

    /**find spuDetail by spuId
     * 修改商品回显 spuDetail
     * 查询 sku 及库存回显
     * @param spuId
     * @return
     */
    public SpuDetil getSpuDetail(Long spuId){
        SpuDetil spuDetil = new SpuDetil();
        spuDetil.setSpuId(spuId);
        return spuDetailMapper.selectByPrimaryKey(spuId);
    }

    /**
     * 查询 sku 及库存回显
     * @param spuId
     * @return
     */
    public List<Sku> getSkuBySpuId(Long spuId){
        //通过spuid查询 Listsku
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus = skuMapper.select(sku);

        //通过skuid查询出stock 库存
        skus.forEach(sku1 -> {
            Stock stock = new Stock();
            //赋值给sku1来回显到前端
            sku1.setStock(stockMapper.selectBySkuId(sku1.getId()).getStock());

        });


        return skus;
    }

    /**
     * update by SpuBo
     * @param spuBo
     */
    @Transactional
    public void updateTheGoods(SpuBo spuBo){
        // 修改 spu
        Goods spu = new Goods();
        //copy spuBo对象给 spu (属性的拷贝)
        BeanUtils.copyProperties(spuBo,spu);
        spu.setLastUpdateTime(new Date());
        spu.setSaleable(null);//上下架
        spu.setValid(null);//是否生效 这里不赋值是因为不需要在修改控制
        mapper.updateByPrimaryKeySelective(spu);

        // 修改 spuDetail
        SpuDetil spuDetail = spuBo.getSpuDetail();
        spuDetailMapper.updateByPrimaryKeySelective(spuDetail);


        //因为sku里的东西是随时变换的 所以我们需要先进行删除 再添加 up-> del-> insert
        //调用删除 skuAndStock方法
        this.deleteSkuAndStockBySpuId(spuBo.getId());

        //调用 增加 sku 和 stock 方法
        this.insertSkuAndStock(spuBo.getSkus(),spuBo.getId());

        amqpTemplate.convertAndSend(MessageRabbitmq.SPU_EXCHANGE_NAME,MessageRabbitmq.SPU_UPDATE_MQ,spu.getId());
    }


    /**
     *  删除 stock 和 sku 修改时调用
     * deleteSkuAndStockBySpuId
     * @param spuId
     */
    private void  deleteSkuAndStockBySpuId(Long spuId){

//        DeleteByIdListMapper<Stock,Long> 当然也可以在mapper层去继承 通过 id的list集合去删除
        //删除库存
        skuMapper.selectIdBySpu(spuId).forEach(sku -> {
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stockMapper.delete(stock);
        });
        //删除 sku
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        skuMapper.delete(sku);
    }


    /**
     * delete by spuId 删除商品 both
     * @param spuId
     */
    @Transactional
    public void deleteBySpuId(Long spuId){
        //通过spuId查询 出 sku中的 sku id 执行 stock中的删除
       this.deleteSkuAndStockBySpuId(spuId);

        //删除 spuDetail by spuId
        SpuDetil spuDetil = new SpuDetil();
        spuDetil.setSpuId(spuId);
        spuDetailMapper.delete(spuDetil);

        //删除 spu by spuId
        mapper.deleteByPrimaryKey(spuId);
    }

    /**t
     * 上下架状态的更改
     * @param saleable
     * @param id
     */
    @Transactional
    public void updateSaleable(Boolean saleable,Long id){
        Goods goods = new Goods();
        goods.setSaleable(saleable);
        goods.setId(id);
        mapper.updateByPrimaryKeySelective(goods);
    }


    public Goods getSpuBySpuId(Long spuId){

        return mapper.selectByPrimaryKey(spuId);
    }


    /**
     * 根据skuId查询sku
     * @param skuId
     * @return
     */
    public Sku getSkuById(Long skuId){

        return skuMapper.selectByPrimaryKey(skuId);
    }

}