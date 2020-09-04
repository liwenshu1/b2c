package com.mr.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mr.client.BrandClient;
import com.mr.client.CategoryClient;
import com.mr.client.GoodsClient;
import com.mr.client.SpecClient;
import com.mr.common.utils.JsonUtils;
import com.mr.common.utils.PageResult;
import com.mr.dao.GoodsRepository;
import com.mr.espojo.Goodes;
import com.mr.espojo.PageSearch;
import com.mr.service.pojo.*;
import com.mr.utils.HignLightUtil;
import com.mr.utils.SearchPage;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GoodsIndexService {

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecClient specClient;

    @Autowired
    private GoodsRepository goodsRespository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;


    //查询es中需要的数据
    public PageResult getGoodEsForEs(PageSearch pageSearch){
        //设置page的值 避免页数多1
        int page=pageSearch.getPage();
        page= page>0? page-1:page;

        //创建构造器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

       //设置分页
        queryBuilder.withPageable(PageRequest.of(page,pageSearch.getSize()));

        //设置条件查询
        if(StringUtils.isNotEmpty(pageSearch.getKey())){
            queryBuilder.withQuery(QueryBuilders.matchQuery("all",pageSearch.getKey()));
        }


        //设置显示字段
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[] {"id","all","skus"},null));

        //设置高亮字段
        queryBuilder.withHighlightFields(new HighlightBuilder.Field("all").preTags(" <font color= 'red' > ").postTags(" </font> "));

        //将商品分类 和品牌 以及规格分组 满足搜索过滤
        queryBuilder.addAggregation(AggregationBuilders.terms("cateGos").field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms("brandGos").field("brandId"));


        //处理规格使用过滤 不会影响成绩
        //创建bool查询
        BoolQueryBuilder boolQueryBuilder=new BoolQueryBuilder();
        //获得过滤字段
        Map<String, String> filterMap = pageSearch.getFilter();

        //判断过滤字段非空后循环拼接 bool查询
        if(filterMap!=null && filterMap.size()!=0){
            filterMap.forEach((key,value)->{
                MatchQueryBuilder matchQueryBuilder=null;
                if(key.equals("cid3") || key.equals("brandId")){
                    matchQueryBuilder=QueryBuilders.matchQuery(key,value);
                }else{
                    matchQueryBuilder=QueryBuilders.matchQuery("specs."+key+".keyword",value);
                }
                boolQueryBuilder.must(matchQueryBuilder);
            });

            queryBuilder.withFilter(boolQueryBuilder);
        }

        //查询
        Page<Goodes> pageResult =goodsRespository.search(queryBuilder.build());

        //if(StringUtils.isNotEmpty(pageSearch.getKey())){
            //获得并替换替换高亮字段
            Map<Long,String> map= HignLightUtil.getHignLigntMap(elasticsearchTemplate,queryBuilder.build(), Goodes.class,"all");
            pageResult.getContent().forEach(goods -> {
                goods.setAll(map.get(goods.getId()));
            });
        //}


        //查询分组结果 通过 AggregationPage去接收
        AggregatedPage<Goodes> aggregatedPage =(AggregatedPage<Goodes>)pageResult;
        //根据分组的组名获取对应的数据 通过LongTerms去接收
        LongTerms cateTerms =(LongTerms)aggregatedPage.getAggregation("cateGos");
        LongTerms brandTerms =(LongTerms)aggregatedPage.getAggregation("brandGos");
        //cateTerms.getBucket来获取 组中的数据
        List<LongTerms.Bucket> cateTermsBuckets = cateTerms.getBuckets();
        //循环获取cateid集合
//        List<Long> cateIds = cateTermsBuckets.stream().map(bucket -> {
//            return bucket.getKeyAsNumber().longValue();
//        }).collect(Collectors.toList());
        //通过cateIds查询 分类信息
        //List<Category> categoryList = categoryClient.queryCateNameByIds(cateIds);


        //获取brand的buckets
        List<LongTerms.Bucket> brandTermsBuckets = brandTerms.getBuckets();
        //循环获取其Ids
        List<Long> brandIds = brandTermsBuckets.stream().map(bucket -> {
           return bucket.getKeyAsNumber().longValue();
        }).collect(Collectors.toList());

        //通过ids查询 品牌的信息
        List<Brand> brandList = brandClient.queryBrandByIds(brandIds);

        //按照分类中的docCount最多的来显示规格聚合
        //首先定义两个常量 为了确保在 lambuda表达式中能使用
        //maxDocCount最大的数据条数                    //默认长度
        final List<Long> maxDocCount=new ArrayList<>(1);
        //添加默认值方便比较
        maxDocCount.add(0l);
        //maxDocCateId 数据最多的Cid是
        final List<Long> maxDocCateId=new ArrayList<>(1);
        maxDocCateId.add(0l);

        //循环cate的聚合bucket 来进行 最大数据条数的比较
        List<Long> cateIds =cateTermsBuckets.stream().map(bucket -> {
            //循环比较数据条数 取得聚合数据条数的max 并将其赋值给 maxDocCount 同时用 maxDocCateId记录分类数据条数最多的id
            //方便进行规格的查询
            if(maxDocCount.get(0)<bucket.getDocCount()){
                maxDocCateId.set(0,bucket.getKeyAsNumber().longValue());
                maxDocCount.set(0,bucket.getDocCount());
            }
            return bucket.getKeyAsNumber().longValue();
        }).collect(Collectors.toList());

        //cateIds查询 分类 返回至 前端
        List<Category> categoryList = categoryClient.queryCateNameByIds(cateIds);


        List<Map<String, Object>> specMap=null;
        //最大热度的 (分类条数最多的 规格查询)
        //首先判断 maxDocCount!= 0l 最大条数不等于0
        if(maxDocCount.get(0)!=0l){
             specMap = this.getSpecMap(maxDocCateId.get(0), pageSearch);
        }

        //获取分页的总页数
        Long total= pageResult.getTotalElements();
        long totalPage  = (long)Math.ceil(total.doubleValue()/pageSearch.getSize());

        //return new PageResult<Goodes>(pageResult.getTotalElements(),totalPage,pageResult.getContent());

        return new SearchPage(pageResult.getTotalElements(),totalPage,pageResult.getContent(),categoryList,brandList,specMap);
    }


    private List<Map<String,Object>> getSpecMap(Long cid,PageSearch pageSearch){

        //创建存储specMap的list集合
        List<Map<String,Object>> list=new ArrayList<>();

        //通过cid 查询出 规格 集合
        List<SpecParam> specParams = specClient.params(null, cid, true,null);



        //创建构造器
        NativeSearchQueryBuilder queryBuilder =new NativeSearchQueryBuilder();

        //设置查询条件 基本查询
        if(StringUtils.isNotEmpty(pageSearch.getKey())){
            queryBuilder.withQuery(QueryBuilders.matchQuery("all",pageSearch.getKey()));
        }



        //添加每个规格的分组
        specParams.forEach(specParam -> {
            //获得 每个规格的 name
            String key = specParam.getName();

            //给每个 key 添加聚合 进行区间分组
            queryBuilder.addAggregation(AggregationBuilders.terms(key).field("specs."+key+".keyword"));
        });

        //获得查询结果
        AggregatedPage<Goodes> page =(AggregatedPage<Goodes>)goodsRespository.search(queryBuilder.build());

        //循环 规格 像map中添加 键值对信息
        specParams.forEach(specParam -> {
            Map<String,Object> map =new HashMap<>();

            //添加集合中的key
            map.put("key",specParam.getName());

            //通过分组的组名 specParam.getName() 也就是key 来获取 对应的聚合参数
            StringTerms stringTerms = (StringTerms)page.getAggregation(specParam.getName());

                //获取buckets集合
            List<StringTerms.Bucket> buckets = stringTerms.getBuckets();

//            {
//                "key": "2.0-2.5GHz",
//                    "doc_count": 69
//            },
//            {
//                "key": "1.0-1.5GHz",
//                    "doc_count": 63
//            },
            //通过循环 buckets 来获取 values (buckets中key的toString)
           List<String>  values = buckets.stream().map(bucket -> {

                return bucket.getKeyAsString();
            }).collect(Collectors.toList());


           map.put("values",values);

           list.add(map);
        });

        return list;
    }

    //将数据库中的数据取出
    public Goodes buildGoodsBySpu(Goods spu){
        Goodes goodes = new Goodes();
        goodes.setId(spu.getId());
        goodes.setBrandId(spu.getBrandId());
        goodes.setCid1(spu.getCid1());
        goodes.setCid2(spu.getCid2());
        goodes.setCid3(spu.getCid3());
        goodes.setCreateTime(spu.getCreateTime());
        goodes.setSubTitle(spu.getSubTitle());

        //查询 brand name 赋值给 all
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        //查询商品分类集合 通过ids
       List<Category> list  = categoryClient.queryCateNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        //取出单独 名称 一列 进行返回
        List<String> nameList=list.stream().map(category -> {
            return category.getName();
        }).collect(Collectors.toList());

        goodes.setAll(spu.getTitle()+""+brand.getName()+""+nameList.toString());    //商品分类 品牌  title

        List<Sku> skuList = goodsClient.getSkuBySpuId(spu.getId());

        //填充价格 到集合 price 集合
        List<Long> priceList = new ArrayList<>();


        //抽取需要的数据 ,不需要的不要 id 图片第一张 和价格 title
        List<Map> skuMapList= new ArrayList<>();

        skuList.forEach(sku->{
           Map map = new HashMap<>();
            map.put("id",sku.getId());
            map.put("price",sku.getPrice());
            map.put("title",sku.getTitle());
            map.put("image", StringUtils.isEmpty(sku.getImages())? "":sku.getImages().split(",")[0]);
//            //赋值stoke
//            map.put("stoke",sku.getStock());
            skuMapList.add(map);

            priceList.add(sku.getPrice());
        });
        //在搜索页面 只需要 id 图片第一张 和价格 title
        goodes.setSkus(   JsonUtils.serialize(skuMapList));

        //填充价格
        goodes.setPrice(priceList);


        //查询 specParam
        List<SpecParam> specParams = specClient.params(null,spu.getCid3(), true,null);
        //查询 spuDetail
        SpuDetil spuDetails = goodsClient.getSpuDetails(spu.getId());

        //通用字段 品牌 上市时间等  spuDetails.getGenericSpec()
        //特有属性  spuDetails.getSpecialSpec()


        //将数据转换成Map格式 方便储存
        // json字符串 不好做匹配，  把json转为对象
        Map<Long, String> genMap = JsonUtils.parseMap(spuDetails.getGenericSpec(), Long.class, String.class);
        Map<Long,List<String>>  specMap=JsonUtils.nativeRead(spuDetails.getSpecialSpec(),
                new TypeReference<Map<Long,List<String>>>(){
                });


        //取出SpuDetilgeneric_spec(特殊规格) 用于查询
         Map<String,Object> goodsSpecMap = new HashMap<>();
        specParams.forEach(specParam->{
            Object value=null;
            //判断是否为共有属性
            if(specParam.getGeneric()){
                value=genMap.get(specParam.getId());//共有属性 通用
                if(specParam.getNumeric()){//判断是否为数字参数
                    //是数值加工 变成段
                    value=buildSegment(value.toString(),specParam);
                }
            }else{
                value=specMap.get(specParam.getId());//特有属性
            }
            goodsSpecMap.put(specParam.getName(),value);
        });

        //将商品的规格 拼接

        goodes.setSpecs(goodsSpecMap);

        return goodes;
    }

    /**
     * 构建段  //"5.2"---》5.0-5.5英寸
     * @param value
     * @param p
     * @return
     */
    private String buildSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }
}
