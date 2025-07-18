package com.gaurav.taskmanager2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Taskmanager2Application {

	public static void main(String[] args) {
		SpringApplication.run(Taskmanager2Application.class, args);
		System.out.println("Application started");
	}

}
