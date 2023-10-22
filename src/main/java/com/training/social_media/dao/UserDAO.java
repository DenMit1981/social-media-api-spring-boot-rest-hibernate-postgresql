package com.training.social_media.dao;

import com.training.social_media.model.User;

import java.util.List;

public interface UserDAO {

    void save(User user);

    User getByLogin(String login);

    User getById(Long id);

    List<User> getAll();

    void update(User user);
}
