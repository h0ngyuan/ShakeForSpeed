package com.sfs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

/**
 * Gateway Service启动类
 * 作为API网关，处理路由转发、身份验证、限流等功能
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
        System.out.println("========================================");
        System.out.println("      SFS Gateway Service Started       ");
        System.out.println("             Port: 9999                ");
        System.out.println("========================================");
    }

}
