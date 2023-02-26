package com.push;

import com.push.core.DataPushServer;
import com.push.simple.GeneralPushServer;
import com.push.utils.SpringBeanLocator;
import com.push.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;

@Slf4j
@EnableScheduling
@SpringBootApplication
@MapperScan("com.push.mapper")
public class App implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        long start = System.currentTimeMillis();
        log.info("start ... {}", start);

        // 功性能推送业务
//        GeneralPushServer generalPushServer = SpringBeanLocator.getBean(GeneralPushServer.class);
//        generalPushServer.start();

        // 优化后推送逻辑
        DataPushServer dataPushServer = SpringBeanLocator.getBean(DataPushServer.class);
        dataPushServer.start();

        long cost = System.currentTimeMillis() - start;
        log.info("cost {}ms", cost);

    }
}
