package com.epam.config;

import com.mongodb.client.MongoClient;
import lombok.SneakyThrows;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MongoConfigTest {
    private MongoConfig mongoConfig = new MongoConfig();

    @Ignore
    @Test
    public void testMongoConfig() {
        MongoClient client = mongoConfig.mongo();

        assertNotNull(client);
    }

    @Ignore
    @SneakyThrows
    @Test
    public void testMongoTemplate() {
        MongoTemplate template = mongoConfig.mongoTemplate();

        assertNotNull(template);
    }
}
