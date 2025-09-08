package com.eams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@PropertySource(value = {"jdbc.properties"}, ignoreResourceNotFound = true)
@EnableAsync
@EnableScheduling
@EnableCaching
@EnableAspectJAutoProxy
public class Eams1Application {

	public static void main(String[] args) {
		SpringApplication.run(Eams1Application.class, args);
	}

}
