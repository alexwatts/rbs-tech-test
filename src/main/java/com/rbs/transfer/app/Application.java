package com.rbs.transfer.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.rbs.transfer")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}