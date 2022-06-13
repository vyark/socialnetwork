package com.epam.repository;

import com.epam.model.Message;

public interface MessageRepository {

    Message findMessageByUserId(Long userId);
}