package com.almacenamiento.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class AlmacenamientoLaGordaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlmacenamientoLaGordaApplication.class, args);
    }

}
