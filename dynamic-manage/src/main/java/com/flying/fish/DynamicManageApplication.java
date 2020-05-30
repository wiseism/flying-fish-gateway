package com.flying.fish;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Description 动态路由配置管理
 * @Author jianglong
 * @Date 2020/05/27
 * @Version V1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
public class DynamicManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(DynamicManageApplication.class, args);
    }

}
