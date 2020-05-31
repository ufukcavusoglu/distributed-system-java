package com.example.DistributedImageParser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.example.DistributedImageParser"})
@EnableScheduling
public class BcClipperImgparserApplication {
    public static void main(String[] args) {
        SpringApplication.run(BcClipperImgparserApplication.class, args);
    }

}
