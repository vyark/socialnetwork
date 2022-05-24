package com.epam.converter;

import com.epam.model.User;
import com.mongodb.DBObject;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserConverterTest {
    private UserConverter userConverter = new UserConverter();

    @SneakyThrows
    @Test
    public void testConvert() {
        User user = User.builder()
                .id(1L)
                .name("Olga")
                .surname("Yarkouskaya")
                .dateOfBirth("01/08/1985")
                .city("Minsk").build();

        DBObject result = userConverter.convert(user);

        DBObject dbObject = com.mongodb.BasicDBObject.parse(" {\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"Olga\",\n" +
                "    \"surname\": \"Yarkouskaya\",\n" +
                "    \"date_of_birth\": \"01/08/1985\",\n" +
                "    \"city\": \"Minsk\"\n" +
                "  }");

        assertEquals(result, dbObject);
    }
}
