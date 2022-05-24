package com.epam.service.impl;

import com.epam.model.User;
import com.epam.repository.UserRepository;
import com.epam.service.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @SneakyThrows
    @Override
    @Transactional
    public User getUser(Long id) {
        return (User) userRepository.findById(id)
                .orElseThrow(() ->
                        new Exception(String.format("User with id = %s is not found", id)));
    }

    @Override
    public void save(User build) {
        userRepository.save(build);
    }
}
