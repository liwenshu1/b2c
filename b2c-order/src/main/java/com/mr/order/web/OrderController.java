package com.mr.order.web;

import com.mr.bo.UserInfo;
import com.mr.common.utils.CookieUtils;
import com.mr.common.utils.PageResult;
import com.mr.config.JwtConfig;
import com.mr.order.bo.OrderBo;
import com.mr.order.pojo.Order;
import com.mr.order.service.OrderService;
import com.mr.util.JwUtils;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@Api("订单服务接口")//swgger2 注解
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtConfig jwtConfig;


    /**
     * 根据前台传递的 cartList和地址 支付方式 来生成订单
     * 并返回订单id
     * @param orderBo
     * @param request
     * @return
     */
    @PostMapping("createOrder")
    @ApiOperation(value="创建接口,返回订单编号",notes="创建订单")
    @ApiImplicitParam(name="createOrder",required = true,value = "订单的json对象,包含skuid,购买数量")
    public ResponseEntity<Long> createOrder(
            @RequestBody OrderBo orderBo,
            HttpServletRequest request){

        //获得 登录数据
        String token = CookieUtils.getCookieValue(request, jwtConfig.getCookieName());
        try{
            //获得 用户信息
            UserInfo userInfo = JwUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            Long id = orderService.createOrder(orderBo, userInfo);
            return  new ResponseEntity<>(id, HttpStatus.CREATED);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }


    }

    /**
     * 根据orderId查询订单信息 并返回
     * @param orderId
     * @return
     */
    @GetMapping("getOrderById/{id}")
    @ApiOperation(value="根据订单查询,返回订单对象",notes = "查询订单")
    @ApiImplicitParam(name="id",required = true,value = "订单编号")
    public ResponseEntity<Order> getOrderById(
            @PathVariable("id") Long orderId
    ){
        Order order = orderService.getOrderById(orderId);
        if(order==null){
            return new ResponseEntity<>(null);
        }
        return  ResponseEntity.ok(order);
    }



    @PutMapping("/updateOrder/{id}/{status}")
    @ApiOperation(value="更新订单状态",notes = "更新状态")
    @ApiImplicitParams({
           @ApiImplicitParam(name = "id",required = true,value = "订单编号"),
           @ApiImplicitParam(name = "status",required = true,value = "订单状态")
    })
    @ApiResponses({
            @ApiResponse(code=204,message = "true 请求成功 更新成功"),
            @ApiResponse(code=401,message = "false 请求成功 更新失败"),
            @ApiResponse(code=405,message = "false 查询失败")
    })
    public ResponseEntity<Boolean> updaTheOrderStatus(
            @PathVariable(value = "id") Long orderId,
            @PathVariable("status") Integer status
    ){

        try {
            Boolean aBoolean = orderService.updateStatus(orderId, status);
            if(aBoolean){
                return new ResponseEntity<>(aBoolean,HttpStatus.NO_CONTENT);//请求成功204
            }else{
                return new ResponseEntity<>(aBoolean,HttpStatus.UNAUTHORIZED);//更新失败/参数错误 401
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);//请求错误 405
        }

    }


    @GetMapping("list")
    @ApiOperation(value="查询订单",notes = "查询订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",defaultValue = "1",value = "页数"),
            @ApiImplicitParam(name = "rows",defaultValue = "5",value = "每页行数"),
            @ApiImplicitParam(name = "status",required = false,value = "订单状态：1未付款，2已付款未发货，3已发货未确认，4已确认未评价，5交易关闭，6交易成功，已评价")
    })
    @ApiResponses({
            @ApiResponse(code=200,message = "订单分页结果ok"),
            @ApiResponse(code=404,message = "没有查询到结果"),
            @ApiResponse(code=500,message = "查询失败"),
    })
    public ResponseEntity<PageResult<Order>> getOrderData(
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows,
            @RequestParam(value = "status",required = false) Integer status,
            HttpServletRequest request
    ){
        //获取Cookie数据
        String token = CookieUtils.getCookieValue(request, jwtConfig.getCookieName());

        //获取用户数据
        try {
            UserInfo userInfo = JwUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            PageResult<Order> orderData = orderService.getOrderData(page, rows, status, userInfo);

            return ResponseEntity.ok(orderData);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

}
