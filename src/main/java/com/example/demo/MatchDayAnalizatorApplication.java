package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "com.example.demo" })
@Slf4j
public class MatchDayAnalizatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(MatchDayAnalizatorApplication.class, args);
	}

}

