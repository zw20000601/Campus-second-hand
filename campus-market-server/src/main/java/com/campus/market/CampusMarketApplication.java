package com.campus.market;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 校园二手交易平台 - 启动类
 */
@SpringBootApplication
@MapperScan({"com.campus.market.module.**.mapper", "com.campus.market.admin.mapper"})
public class CampusMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusMarketApplication.class, args);
        System.out.println("""
                ==========================================
                   校园二手交易平台 启动成功 🎉
                   API文档: http://localhost:8080/doc.html
                ==========================================
                """);
    }
}
