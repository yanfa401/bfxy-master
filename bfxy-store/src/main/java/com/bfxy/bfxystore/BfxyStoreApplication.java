package com.bfxy.bfxystore;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan({"com.bfxy.bfxystore.*"})
@MapperScan("com.bfxy.bfxystore.mapper")
public class BfxyStoreApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(BfxyStoreApplication.class, args);
    }
    
}
