package com.ruoyi.nocode.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 网关服务启动类
 * 
 * @author ruoyi-nocode
 */
@EnableDiscoveryClient
@SpringBootApplication
public class RuoYiNoCodeGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(RuoYiNoCodeGatewayApplication.class, args);
        System.out.println("===== 网关服务启动成功 =====");
    }
}
