package com.example.pagoclinica.pagoclinica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PagoclinicaApplication {

	public static void main(String[] args) {
		SpringApplication.run(PagoclinicaApplication.class, args);
	}

}
