package com.example.mongo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix="mongo.db")
public class MongoCredentialConfig {
    private Map<String, String> urls;
}
