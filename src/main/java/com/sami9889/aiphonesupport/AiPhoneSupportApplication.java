package com.sami9889.aiphonesupport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AiPhoneSupportApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiPhoneSupportApplication.class, args);
    }
}
