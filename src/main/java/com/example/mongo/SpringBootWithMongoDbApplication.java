package com.example.mongo;

import com.example.mongo.config.MongoConfig;
import com.example.mongo.config.MongoCredentialConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@Slf4j
public class SpringBootWithMongoDbApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWithMongoDbApplication.class, args);
	}

	@Bean(name = "mongoTemplateMap")
	public Map<String, MongoTemplate> getMongoTemplateMap(final MongoCredentialConfig mongoCredentialConfig){
		log.info("Creating MongoTemplate ....");
		final Map<String, MongoTemplate> templateMap = new HashMap<>();
		mongoCredentialConfig.getUrls().forEach((database,url)-> {
			templateMap.put(database, MongoConfig.createMongoTemplate(database, url));
		});
		return templateMap;
	}

}
