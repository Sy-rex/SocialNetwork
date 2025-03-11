package com.sobolev.spring.chatservice.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration{

    @Override
    protected String getDatabaseName() {
        return "chatdb";
    }

    @Bean
    public MongoTemplate mongoTemplate(){
        return new MongoTemplate(MongoClients.create(getConnectionString()), getDatabaseName());
    }

    private ConnectionString getConnectionString(){
        return new ConnectionString("mongodb://mongo:mongo123@localhost:27017/chatdb?authSource=admin");
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder){
        builder.applyConnectionString(getConnectionString());
    }
}
