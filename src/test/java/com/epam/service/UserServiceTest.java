package com.epam.service;

import com.epam.config.MongoConfig;
import com.epam.model.User;
import com.epam.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MongoConfig.class)
@SpringBootTest
public class UserServiceTest {

    private UserService service = new UserServiceImpl();

    @Test
    public void test() {
        service.save(User.builder().id(1L).name("John").surname("Smith").dateOfBirth("01/01/2000").city(
                "Krakow").build());

        User result = service.getUser(1L);

        System.out.println(result);
    }
}
