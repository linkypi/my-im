package com.hiraeth.im.business;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author: lynch
 * @description:
 * @date: 2023/11/23 12:39
 */
@Slf4j
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.hiraeth.im.business.mapper")
public class BusinessApplication {
    public static void main(String[] args) {
        System.setProperty("rocketmq.client.logUseSlf4j", "true");
        System.setProperty("rocketmq.client.logLevel", "ERROR");

        SpringApplication.run(BusinessApplication.class);
        log.info("business server started.");
    }
}
