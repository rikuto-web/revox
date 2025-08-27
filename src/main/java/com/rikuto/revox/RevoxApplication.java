package com.rikuto.revox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;
import javax.annotation.PostConstruct;

@SpringBootApplication
public class RevoxApplication {

	@Value("${DB_USER:undefined}")
	private String dbUser;

	@Value("${DB_PASSWORD:undefined}")
	private String dbPassword;

	@Value("${PROD_DB_URL:undefined}")
	private String prodDbUrl;

	public static void main(String[] args) {
		SpringApplication.run(RevoxApplication.class, args);
	}

	@PostConstruct
	public void logEnv() {
		System.out.println("===== ENV DEBUG =====");
		System.out.println("DB_USER = " + dbUser);
		System.out.println("DB_PASSWORD = " + (dbPassword.equals("undefined") ? "undefined" : "********"));
		System.out.println("PROD_DB_URL = " + prodDbUrl);
		System.out.println("=====================");
	}
}
