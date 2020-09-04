package com.mr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableDiscoveryClient//标志为客户端
@EnableZuulProxy //启动zuul
public class GatewaysApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewaysApplication.class,args);
    }
}
