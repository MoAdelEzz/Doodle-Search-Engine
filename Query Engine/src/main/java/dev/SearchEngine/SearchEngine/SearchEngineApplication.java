package dev.SearchEngine.SearchEngine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@SpringBootApplication
public class SearchEngineApplication {

	public static void main(String[] args) {
		MyConfiguration conf = new MyConfiguration();
		CorsRegistry registry = new CorsRegistry();
		conf.addCorsMappings(registry);
		SpringApplication.run(SearchEngineApplication.class, args);
	}
}
