package com.hungshop.hunghypebeast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HungHypebeastApplication {

    public static void main(String[] args) {
        SpringApplication.run(HungHypebeastApplication.class, args);
    }

}
