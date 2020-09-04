package com.mr.web;

import com.mr.bo.UserInfo;
import com.mr.config.JwtConfig;
import com.mr.pojo.Cart;
import com.mr.service.CartService;
import com.mr.util.JwUtils;
import io.jsonwebtoken.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CartController {

        @Autowired
        private CartService service;

        @Autowired
        private JwtConfig jwtConfig;

        @PostMapping("addCart")
        public void addCart(
                @RequestBody Cart cart,
               @CookieValue("B2C_TOKEN") String token) {

            try{
                UserInfo userInfo = JwUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
                service.addCartList(cart,userInfo);
            }catch (Exception e){
                e.printStackTrace();
            }

        }


    /**
     * 查询购物车数据
     * @return
     */
    @GetMapping("queryCartList")
        public ResponseEntity<List<Cart>> queryCartList(@CookieValue("B2C_TOKEN") String token){

        try{
            //利用公钥解密 获得登录数据
            UserInfo userInfo = JwUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            List<Cart> cartList = service.getCartList(userInfo);

            if(cartList==null){

                ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            return ResponseEntity.ok(cartList);


        }catch (Exception e){
            e.printStackTrace();
        }

            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

    /**
     * 修改商品数量 num
     * @param cart
     * @param token
     * @return
     */
        @PutMapping("updateNum")
        public ResponseEntity updateNum(
                @RequestBody Cart cart ,
                @CookieValue("B2C_TOKEN") String token ){

        try{
            //根据 公钥 对token进行解密取出 user信息
            UserInfo userInfo = JwUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            //调用service层方法
            service.updateNum(cart,userInfo);
            return ResponseEntity.ok(null);
        }catch (Exception e){
            e.printStackTrace();
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    /**
     * 删除 购物车 中对应的商品
     * @param skuId
     * @param token
     * @return
     */
    @DeleteMapping("delCart/{skuId}")
    public ResponseEntity delCart(
            @PathVariable("skuId") Long skuId,
            @CookieValue("B2C_TOKEN") String token){

        try{
            //根据 公钥 对token进行解密取出 user信息
            UserInfo userInfo = JwUtils.getInfoFromToken(token, jwtConfig.getPublicKey());


            service.delCart(skuId,userInfo);

            return ResponseEntity.ok(null);

        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }


    }
 }
