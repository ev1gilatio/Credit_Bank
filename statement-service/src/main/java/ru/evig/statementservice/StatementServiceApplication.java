package ru.evig.statementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class StatementServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(StatementServiceApplication.class, args);
	}
}
