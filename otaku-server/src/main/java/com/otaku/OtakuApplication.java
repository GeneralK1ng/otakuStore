package com.otaku;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement  //事务管理
@Slf4j
public class OtakuApplication {
    public static void main(String[] args) {
        SpringApplication.run(OtakuApplication.class, args);
        log.info("server started");
    }
}
