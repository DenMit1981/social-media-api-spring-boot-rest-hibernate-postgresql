package com.training.social_media.dao.impl;

import com.training.social_media.dao.UserDAO;
import com.training.social_media.exception.UserNotFoundException;
import com.training.social_media.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@AllArgsConstructor
public class UserDAOImpl implements UserDAO {

    private static final String QUERY_SELECT_FROM_USER = "from User";
    private static final String USER_NOT_FOUND = "User with login %s not found";
    private static final String USER_NOT_FOUND_BY_ID = "User with id %s not found";

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public void save(User user) {
        entityManager.persist(user);
    }

    @Override
    public User getByLogin(String login) {
        return getAll().stream()
                .filter(user -> login.equals(user.getEmail()))
                .findAny()
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND, login)));
    }

    @Override
    public User getById(Long id) {
        return getAll().stream()
                .filter(user -> id.equals(user.getId()))
                .findAny()
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND_BY_ID, id)));
    }

    @Override
    public List<User> getAll() {
        return entityManager.createQuery(QUERY_SELECT_FROM_USER, User.class)
                .getResultList();
    }

    @Override
    public void update(User user) {
        entityManager.merge(user);
    }
}
