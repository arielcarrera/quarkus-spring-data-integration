package com.github.arielcarrera.quarkus.spring.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan(basePackages = "*")
@SpringBootApplication
public class SpringDataJpaServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringDataJpaServiceApplication.class, args);
	}

}
