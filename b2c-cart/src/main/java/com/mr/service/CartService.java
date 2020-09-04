package com.mr.service;

import com.mr.bo.UserInfo;
import com.mr.client.GoodsClient;
import com.mr.common.utils.JsonUtils;
import com.mr.pojo.Cart;
import com.mr.service.pojo.Sku;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GoodsClient goodsClient;

    //定义redis中的key
    static final String KEY_PREFIX="b2c:cart:uid:";

    /**
     * 增加购物车中的商品信息到redis
     * @param cart
     * @param userInfo
     */
    public void addCartList(Cart cart, UserInfo userInfo){

        //增加key用户id
        String key=KEY_PREFIX+userInfo.getId();

        //创建redis
        //获取hash操作对象
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);

        //取出商品id
        Long skuId=cart.getSkuId();
        Integer num=cart.getNum();

        //查询是否购买过/是否添加过购物车
        Boolean aBoolean = hashOps.hasKey(skuId.toString());


        //购买过
        if(aBoolean){
            String json = hashOps.get(skuId.toString()).toString();
            cart = JsonUtils.parse(json, Cart.class);

            cart.setNum(cart.getNum()+num);


        }else{

            //不存在 没有买过 新增sku信息
            cart.setUserId(userInfo.getId());
            //根据skuId查询sku数据
            Sku sku = goodsClient.getSku(skuId);

            //存入图片
            cart.setImage(StringUtils.isBlank(sku.getImages())? "":StringUtils.split(sku.getImages(),",")[0]);

            cart.setTitle(sku.getTitle());
            cart.setPrice(sku.getPrice());
            cart.setOwnSpec(sku.getOwnSpec());
        }

        //将购物车数据写入 redis中的hash类型

        hashOps.put(cart.getSkuId().toString(),JsonUtils.serialize(cart));

    }


    /**
     * 查询redis数据
     * @param userInfo
     * @return
     */
    public List<Cart> getCartList(UserInfo userInfo){

        //判断用户是否有购物车数据
        String key=KEY_PREFIX+userInfo.getId();

        if(!redisTemplate.hasKey(key)){
            return null;
        }

        //获得绑定key的hash对象
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);

        List<Object> cartList = hashOps.values();

        //判断是否有数据
        if(cartList==null&&cartList.size()==0){

            return null;
        }


        return cartList.stream().map(cart->JsonUtils.parse(cart.toString(),Cart.class)).collect(Collectors.toList());
    }


    /**
     * 根据传递的num 修改 redis 中的 num
     * @param cart
     * @param userInfo
     */
    public void updateNum(Cart cart,UserInfo userInfo){

        //获得 hash key
        String key=KEY_PREFIX+userInfo.getId();
        //取出 cart 中的 num 和 skuId
        Long skuId=cart.getSkuId();
        Integer num=cart.getNum();

//        //判断购物车数据是否存在
//        Boolean aBoolean = redisTemplate.hasKey(key);
//
//        if(aBoolean){

            //获得 hash操作对象
            BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);
            //获取指定商品id 的 商品 json 字符串
            String json = hashOps.get(skuId.toString()).toString();

            //利用工具类进行转换 成实体类 并赋值
            cart = JsonUtils.parse(json, Cart.class);

            //将更改后的数量 重新赋值进cart
            cart.setNum(num);

            //重新将数据放入 hash操作对象中
            hashOps.put(cart.getSkuId().toString(),JsonUtils.serialize(cart));
        }


    //}


    public void delCart(Long skuId,UserInfo userInfo){

        //获得 hash key
        String key=KEY_PREFIX+userInfo.getId();

        //获得 hash操作对象
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);

        hashOps.delete(skuId.toString());


    }

}
