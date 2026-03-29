package com.example.security_kit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.example.security_kit", "core", "auth"})
public class SecurityKitApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityKitApplication.class, args);
	}

}
