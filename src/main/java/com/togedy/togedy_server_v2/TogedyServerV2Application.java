package com.togedy.togedy_server_v2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class TogedyServerV2Application {

    public static void main(String[] args) {
        SpringApplication.run(TogedyServerV2Application.class, args);
    }

}
