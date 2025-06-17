package com.zwash.car;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
@SpringBootApplication
@ComponentScan(basePackages = {
	    "com.zwash.car", 
	    "com.zwash.auth", 
	    "com.zwash.common"
	})
public class ZwashCarManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZwashCarManagementApplication.class, args);
	}

}
