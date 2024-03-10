package com.example.app2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.example.app1", "com.example.app2"})
public class App2Application {
	public static void main(String[] args) {
		SpringApplication.run(App2Application.class, args);
	}
}
