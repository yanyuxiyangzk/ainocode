package com.ruoyi.nocode.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 系统服务启动类
 * 
 * @author ruoyi-nocode
 */
@EnableDiscoveryClient
@MapperScan("com.ruoyi.nocode.**.mapper")
@SpringBootApplication
public class RuoYiNoCodeSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(RuoYiNoCodeSystemApplication.class, args);
        System.out.println("===== 系统服务启动成功 =====");
    }
}
