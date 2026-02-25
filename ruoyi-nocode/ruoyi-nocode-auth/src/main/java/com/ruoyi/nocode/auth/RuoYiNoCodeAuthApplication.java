package com.ruoyi.nocode.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 认证服务启动类
 * 
 * @author ruoyi-nocode
 */
@EnableDiscoveryClient
@SpringBootApplication
public class RuoYiNoCodeAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(RuoYiNoCodeAuthApplication.class, args);
        System.out.println("===== 认证服务启动成功 =====");
    }
}
