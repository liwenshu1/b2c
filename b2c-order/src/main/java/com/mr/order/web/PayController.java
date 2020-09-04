package com.mr.order.web;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.mr.common.utils.JsonUtils;
import com.mr.config.ApliPayConfig;
import com.mr.order.pojo.Order;
import com.mr.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Controller
public class PayController {

    @Autowired
    private OrderService orderService;

    /**
     * 支付
     * @param orderId
     * @param response
     * @return
     * @throws AlipayApiException
     * @throws IOException
     */
    @GetMapping(value="pay/{orderId}")
    public String pay(
            @PathVariable("orderId") Long orderId,
            HttpServletResponse response
    ) throws AlipayApiException, IOException {
        // 获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(ApliPayConfig.GATEWAYURL, ApliPayConfig.APP_ID,
                ApliPayConfig.MERCHANT_PRIVATE_KEY, "json", ApliPayConfig.CHARSET,
                ApliPayConfig.ALIPAY_PUBLIC_KEY,ApliPayConfig.SIGN_TYPE);


        // 设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        //修改订单状态的页面
        alipayRequest.setNotifyUrl(ApliPayConfig.NOTIFY_URL);
        //成功支付后跳转的页面
        alipayRequest.setReturnUrl(ApliPayConfig.RETURN_URL);


        // 付款金额，必填
        Order order=orderService.getOrderById(orderId);

        // 商户订单号，商户网站订单系统中唯一订单号，必填
        String subject=order.getOrderDetails().get(0).getTitle();
        Map<String,String> map = JsonUtils.parseMap(order.getOrderDetails().get(0).getOwnSpec(),String.class,String.class);

        String body =  map.values().toString();
        String orderIdStr=order.getOrderId().toString();
        String payPrice=order.getActualPay().toString();
        String param;
        param= "{"  +
                "    \"out_trade_no\":\""+orderIdStr+"\","  +
                "    \"product_code\":\"FAST_INSTANT_TRADE_PAY\","  +
                "    \"total_amount\":"+payPrice+","  +
                "    \"subject\":\""+subject+"\","  +
                "    \"body\":\""+body+"\""  +
//                "    \"passback_params\":\"merchantBizType%3d3C%26merchantBizNo%3d2016010101111\","  +
//                "    \"extend_params\":{"  +
//                "    \"sys_service_provider_id\":\"2088511833207846\""  +
//                "    }" +
                "  }" ; //填充业务参数
        System.out.println(param);
        alipayRequest.setBizContent(param);
        // 请求
        String form = alipayClient.pageExecute(alipayRequest).getBody();
        // System.out.println(result);
        response.setContentType("text/html; charset=gbk");
        PrintWriter out = response.getWriter();
        out.print(form);
        return null;
    }

    /**
     * 支付成功到回调，成功后回调用此函数
     * 在函数中可以修改订单状态等
     * 但由于此配置必须公网才能使用，暂不使用
     * @param request
     * @return
     */
    @PostMapping("paySuccess")
    public String paySuccess(HttpServletRequest request){
        System.out.println("成功回调");
        Map<String,String[]> map=request.getParameterMap();
        for (String key: map.keySet()) {
            System.out.println(key + "      "+map.get(key));
        }
        return "success";
    };

    /**
     * 支付完成后，跳转的url
     * 由于回调不可使用，我们在这个url中修改订单状态
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @GetMapping("callback")
    public String callback(HttpServletRequest request,HttpServletResponse response) throws IOException {

        //获得订单号
        String orderId=request.getParameter("out_trade_no");
        //修改订单状态
        orderService.updateStatus(Long.valueOf(orderId),2);
        //重定向到我的订单界面
        response.sendRedirect("http://www.b2c.com/home.html");

        return null;
    };
}