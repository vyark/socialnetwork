package com.epam.repository;

import com.epam.model.Friendship;

import java.util.List;

public interface FriendshipRepository {

    List<Friendship> findAllByUserId(Long userId);
}