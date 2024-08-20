package com.multirkh.chimhahaclone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ChimhahaCloneApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChimhahaCloneApplication.class, args);
    }
}
