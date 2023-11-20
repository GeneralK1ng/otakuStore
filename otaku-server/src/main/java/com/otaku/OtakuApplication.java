package com.otaku;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement  //事务管理
@Slf4j
@EnableCaching // 开启缓存注解
@EnableScheduling //开启任务调度
public class OtakuApplication {
        /**
     * 主方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(OtakuApplication.class, args);
        log.info(" \n" +
                " ██████╗ ████████╗ █████╗ ██╗  ██╗██╗   ██╗\n" +
                "██╔═══██╗╚══██╔══╝██╔══██╗██║ ██╔╝██║   ██║\n" +
                "██║   ██║   ██║   ███████║█████╔╝ ██║   ██║\n" +
                "██║   ██║   ██║   ██╔══██║██╔═██╗ ██║   ██║\n" +
                "╚██████╔╝   ██║   ██║  ██║██║  ██╗╚██████╔╝\n" +
                " ╚═════╝    ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝ \n" +
                "                                           \n" +
                "███████╗████████╗ ██████╗ ██████╗ ███████╗ \n" +
                "██╔════╝╚══██╔══╝██╔═══██╗██╔══██╗██╔════╝ \n" +
                "███████╗   ██║   ██║   ██║██████╔╝█████╗   \n" +
                "╚════██║   ██║   ██║   ██║██╔══██╗██╔══╝   \n" +
                "███████║   ██║   ╚██████╔╝██║  ██║███████╗ \n" +
                "╚══════╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝╚══════╝ ");
    }

}
