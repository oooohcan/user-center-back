package org.oooohcan.userback;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.oooohcan.userback.mapper")
public class UserBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserBackApplication.class, args);
    }

}
