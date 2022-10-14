package com.example.mongo.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class MongoConfig {

    private Map<String, MongoTemplate> mongoTemplateMap = new HashMap<>();

    @Value( "${mongo.client.uri}" )
    private String mongoClientUri;

    public MongoTemplate getMongoTemplate(String database) {
        if(mongoTemplateMap.containsKey(database)){
            return mongoTemplateMap.get(database);
        }else{
            try {
                //Replace database in URI with database being sent
                String client = mongoClientUri.replaceFirst("\\{database\\}",database);

                final ConnectionString connectionString = new ConnectionString(client);
                //URI without database
                final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                        .applyConnectionString(connectionString)
                        .build();
                MongoClient mongoClient = MongoClients.create(mongoClientSettings);
                //DB factory created with given database
                SimpleMongoClientDatabaseFactory mongoDbFactory = new SimpleMongoClientDatabaseFactory(mongoClient,database);
                //To get acknowledgements after writes. This will give acknowledgement as soon as it is written into the primary DB
                mongoDbFactory.setWriteConcern(WriteConcern.W1);

                final MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory);
                mongoTemplateMap.put(database, mongoTemplate);
                return mongoTemplate;
            } catch (Exception e) {
                log.error("Error building a Mongo Template. Exception: ", e);
            }
            return null;

        }
    }
}
