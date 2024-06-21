package com.curtis.userspherebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.curtis.userspherebackend.mapper")
public class UserSphereBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserSphereBackendApplication.class, args);
    }
}


