package ru.evig.dossierservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class DossierServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DossierServiceApplication.class, args);
    }
}
