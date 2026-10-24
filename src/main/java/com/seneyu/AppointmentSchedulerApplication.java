package com.seneyu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class AppointmentSchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppointmentSchedulerApplication.class, args);
    }
}
