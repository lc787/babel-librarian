package com.babel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication//(exclude={DataSourceAutoConfiguration.class})
public class BabelApplication {
    public static void main(String[] args) {
        SpringApplication.run(BabelApplication.class, args);
    }

}
