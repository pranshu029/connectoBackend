package com.connectoBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class ConnectoBackendApplication {

	public static void main(String[] args) {

		SpringApplication.run(ConnectoBackendApplication.class, args);
	}
}