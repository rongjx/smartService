package com.dotoyo.archivedb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.dotoyo.archivedb.mapper")
public class ArchivedbApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArchivedbApplication.class, args);
    }

}
