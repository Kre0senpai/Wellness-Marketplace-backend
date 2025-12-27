package com.wellness.wellness_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.wellness")
public class WellnessBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(WellnessBackendApplication.class, args);
	}

}
