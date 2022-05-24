package com.epam.service;

import com.epam.model.User;

public interface UserService {
    User getUser(Long id);

    void save(User build);
}
