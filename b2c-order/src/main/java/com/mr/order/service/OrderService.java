package com.mr.order.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mr.bo.UserInfo;
import com.mr.client.AddressClient;
import com.mr.client.GoodsClient;
import com.mr.common.utils.IdWorker;
import com.mr.common.utils.PageResult;
import com.mr.order.bo.AddressBo;
import com.mr.order.bo.CartBo;
import com.mr.order.bo.OrderBo;
import com.mr.order.mapper.OrderDetailMapper;
import com.mr.order.mapper.OrderMapper;
import com.mr.order.mapper.OrderStatusMapper;
import com.mr.order.pojo.Order;
import com.mr.order.pojo.OrderDetail;
import com.mr.order.pojo.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    //雪花算法
    @Autowired
    private IdWorker idWorker;

    @Autowired
    private GoodsClient goodsClient;

    //订单表
    @Autowired
    private OrderMapper orderMapper;

    //订单详情表
    @Autowired
    private OrderDetailMapper orderDetail;

    //订单状态表
    @Autowired
    private OrderStatusMapper orderStatusMapper;






    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);


    //开启事务
    @Transactional
    public Long createOrder(OrderBo orderBo, UserInfo userInfo){
        //前端只传id信息 ,其余从数据库中查询以防止 数据被篡改 提交

        //初始化订单数据
        Order order = new Order();

        //雪花算法生成 位移订单id
        long orderId = idWorker.nextId();

        order.setCreateTime(new Date());
        order.setOrderId(orderId);

        //支付方式
        order.setPaymentType(orderBo.getPayMentType());
        //订单 中用户信息
        order.setUserId(userInfo.getId());
        order.setBuyerNick(userInfo.getUsername());
        order.setBuyerRate(false);

        //收货人信息(通过地址id来查询)
        AddressBo addressByID = AddressClient.getAddressByID(orderBo.getAddressId());
        //给订单赋值
        //收货人
        order.setReceiver(addressByID.getName());
        //邮编
        order.setReceiverZip(addressByID.getZipCode());
        //手机
        order.setReceiverMobile(addressByID.getPhone());
        //市
        order.setReceiverCity(addressByID.getCity());
        //县
        order.setReceiverState(addressByID.getState());
        //收货地址区
        order.setReceiverDistrict(addressByID.getDistrict());
        //收获地址街道
        order.setReceiverAddress(addressByID.getAddress());

        //总金额
         long totalPays=0l;
        //循环查询 price
//        orderBo.getCartList().forEach(sku->{
//            sku.setSku(goodsClient.getSku(sku.getSkuId()));
//            totalPays += sku.getSku().getPrice() * sku.getNum();
//        });
        for (CartBo cartBo: orderBo.getCartList()) {
            cartBo.setSku(goodsClient.getSku(cartBo.getSkuId()));
            totalPays += cartBo.getSku().getPrice() * cartBo.getNum();
        }
        order.setTotalPay(totalPays);

        //实际付款金额 目前不做 默认总金额
        order.setActualPay(totalPays);
        //保存数据
        orderMapper.insertSelective(order);

        //组装订单 一个订单可能有多个sku商品
        List<OrderDetail> orderDetailList = new ArrayList<>();
        orderBo.getCartList().forEach(cartBo -> {
            OrderDetail orderD = new OrderDetail();
            //sku图片
            orderD.setImage(cartBo.getSku().getImages().split(",")[0]);
            //购买商品数量
            orderD.setNum(cartBo.getNum());
            //特有属性
            orderD.setOwnSpec(cartBo.getSku().getOwnSpec());
            //skuid
            orderD.setSkuId(cartBo.getSkuId());
            //购买时价格
            orderD.setPrice(cartBo.getSku().getPrice());
            //标题
            orderD.setTitle(cartBo.getSku().getTitle());
            //订单id
            orderD.setOrderId(orderId);
            orderDetailList.add(orderD);

        });
        //批量保存保存订单详情数据
        orderDetail.insertList(orderDetailList);

        //保存订单状态
        OrderStatus orderStatus = new OrderStatus();
        //order_id
        orderStatus.setOrderId(orderId);
        //订单创建时间
        orderStatus.setCreateTime(order.getCreateTime());
        //初始是未付款
        orderStatus.setStatus(1);

        //保存订单状态
        orderStatusMapper.insertSelective(orderStatus);

        //订单完成 修改 stock (库存) 超卖问题  解决
        /**
         * 一般来说我们在生成完订单信息 后 可以直接对库存进行修改 这个思路事没问题的
         * 但是 你要考虑 库存的 并发问题 例如 仅剩余一件商品 购买 2个 这样情况下 stock 总不能 是-1
         *
         * 所以这里就需要进行 对数据库查询 stock 当 数量 >0时候
         * 判断库存的有无 才可以 进行 库存的 修改
         *组装订单中 cartBo.getSku().getStock
         * 获取商品的数量进行判断 >购买商品的数量 num 则做订单的生成
         *
         */

        /**
         * 但是这个地方我们得考虑一个问题 那就是 并发 线上 并发环境下 可能是多个用户 同时进行商品的 购买 如果第一个用户的 事务未提交完毕没有进行修改
         * 第二个 第三个用户胡这时候 查询的 数据库 stock数量 仍然是 之前的 数量 2 这样的话 判断就是可以生成该订单 等 事务结束 生成了 n个订单
         *
         * 这种情况我们考虑  加锁
         *      sync 加在方法上 在事务未执行完毕时候后 会让其他 事务等待 执行完毕之后 在执行其他的 事务
         *      这样做安全了 并发的问题也结局了 但是 效率会低了
         *
         *      考虑到效率问题这时候我们可以用乐观锁的 思路 接合 分布式事务  -->
         *              给 特定的 方法/事务 加锁 使得 其他方法/事务不会受到此线程锁的影响
         *              (就是把锁加载 第三方 服务 上 -- mysql redis ... )
         *              从而进行 锁的 减少
         *              从而提高线程安全的 提高并发的同时兼顾 效率
         *
         *              mysql是有行级锁的 比如修改一条数据
         *                  update student set stock=* where skuid=*
         *                  成功会返回 1 失败则是 0
         *                  所以我们可以利用返回的 值来进行 判断 修改是否成功
         *                  来决定是否回滚
         *
         *
         *
         */
                /**
                 *        在分布式 事务中  传统的事务是无法回滚的 因为
                 *          我们在 新增 修改商品 也好 服务都是互相调用的
                 *
                 *           mq 异步确保
                 *                  *          发送 给mq 消息 执行 数据修改 在 错误时候 由于 mq的 ack机制会将消息继续返回给 队列 然后 重复的 执行
                 *                  *             ---> 这个 异步确保得 确保一定修改成功才可以进行使用
                 *
                 *        常用的 分布式事务方案
                 *                  2pc
                 *                  tcc 确认事务  事务失败回滚 ---> 补偿 / 可以 ---> 执行 失败 重调
                 *                      在事务失败后 可以决定重新请求或者发送 指定信息 进行补偿 来让 消费者 / 用户 更容易接受
                 *                      这里的补偿 就是 我们手动 去删除 生成的订单 然后 返回 给用户 生成订单失败 亦或者 是 库存不足 等等
                 *
                 *
                 */





        //返回订单id
        return orderId;
    }



    public Order getOrderById(Long orderId){
        Order order = orderMapper.selectByPrimaryKey(orderId);
        //查询订单详情和商品状态
        OrderDetail orderD = new OrderDetail();
        orderD.setOrderId(orderId);
        List<OrderDetail> select = orderDetail.select(orderD);
        order.setOrderDetails(select);

        //查询 订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        OrderStatus orderStatus1 = orderStatusMapper.selectByPrimaryKey(orderId);

        order.setStatus(orderStatus1.getStatus());


        return order;
    }



    @Transactional
    public Boolean updateStatus(Long orderId,Integer status){
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setStatus(status);

        //根据状态判断 要修改的时间
        switch (status){
            case 2 : orderStatus.setPaymentTime(new Date()); //已付款
                break;
            case 3:    orderStatus.setConsignTime(new Date());//发货时间
                break;
            case 4: orderStatus.setEndTime(new Date());//交易完成时间
                break;
            case 5: orderStatus.setCloseTime(new Date());//交易关闭时间
                break;
            case 6: orderStatus.setCommentTime(new Date());//评价时间
                break;

            default:
                return null;
        }

        int i = orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
        if(i==1){
            return true;
        }
        return false;

    }


    public PageResult<Order> getOrderData(Integer page, Integer rows, Integer status,UserInfo userInfo) {
        try {
            // 设置分页等其起始值，每页条数
            PageHelper.startPage(page, rows);

            // 创建查询条件用户id和状态
            Page<Order> pageInfo = (Page<Order>) this.orderMapper.queryOrderList(userInfo.getId(), status);

            return new PageResult<>(pageInfo.getTotal(), pageInfo);
        } catch (Exception e) {
           e.printStackTrace();
            return null;
        }
    }
}
