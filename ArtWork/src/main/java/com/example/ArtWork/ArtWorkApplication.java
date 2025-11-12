package com.example.ArtWork;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@SpringBootApplication
public class ArtWorkApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArtWorkApplication.class, args);
	}
	

}
