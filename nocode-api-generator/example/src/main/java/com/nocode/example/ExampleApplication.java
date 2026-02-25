package com.nocode.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.nocode.starter.EnableNocodeApi;

/**
 * 示例应用入口
 */
@SpringBootApplication
@EnableNocodeApi(enableAdmin = true)
public class ExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }
}
