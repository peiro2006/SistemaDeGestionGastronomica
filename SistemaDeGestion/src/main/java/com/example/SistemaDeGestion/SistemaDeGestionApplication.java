package com.example.SistemaDeGestion;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SistemaDeGestionApplication {
    public static void main(String[] args) {
        SpringApplication.run(SistemaDeGestionApplication.class, args);
    }
}