package com.epam.repository;

import com.epam.config.MongoConfig;
import com.epam.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MongoConfig.class)
public class UserRepositoryTest {

    @SpyBean
    private UserRepository userRepository;

    @Autowired
    private MongoOperations mongoOps;

    @BeforeEach
    public void testSetup() {
        if (!mongoOps.collectionExists(User.class)) {
            mongoOps.createCollection(User.class);
        }
    }

    @AfterEach
    public void tearDown() {
        mongoOps.dropCollection(User.class);
    }

    @Test
    public void test() {
        userRepository.save(User.builder().id(1L).name("John").surname("Smith").dateOfBirth("01/01/2000").city(
                "Krakow").build());

        User result = userRepository.findUserById(1L);

        System.out.println(result);
    }
}
