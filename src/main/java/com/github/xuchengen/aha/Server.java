package com.github.xuchengen.aha;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.github.xuchengen.aha")
@EnableScheduling
@ServletComponentScan
@EnableAsync
public class Server {

    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }

}
