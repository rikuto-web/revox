package com.rikuto.revox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Revox API",
				version = "1.0.0",
				description = "Google認証を利用したシンプルなWebサービスのAPIドキュメントです。"
		)
)
public class RevoxApplication {

	public static void main(String[] args) {
		SpringApplication.run(RevoxApplication.class, args);
	}
}