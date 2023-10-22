package com.training.social_media.dao.impl;

import com.training.social_media.dao.PostDAO;
import com.training.social_media.exception.PostNotFoundException;
import com.training.social_media.model.Post;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@AllArgsConstructor
public class PostDAOImpl implements PostDAO {

    private static final String QUERY_SELECT_FROM_POST = "from Post";
    private static final String QUERY_SELECT_FROM_POST_CREATED_BY_CURRENT_USER = "from Post p where p.user.email =:userLogin";
    private static final String QUERY_DELETE_FROM_POST_BY_POST_ID = "delete from Post p where p.id =:id";
    private static final String POST_NOT_FOUND = "Post with id %s not found";

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public void save(Post post) {
        entityManager.persist(post);
    }

    @Override
    public List<Post> getAll() {
        return entityManager.createQuery(QUERY_SELECT_FROM_POST, Post.class)
                .getResultList();
    }

    @Override
    public List<Post> getAllCreatedByCurrentUser(String userLogin) {
        return entityManager.createQuery(QUERY_SELECT_FROM_POST_CREATED_BY_CURRENT_USER, Post.class)
                .setParameter("userLogin", userLogin)
                .getResultList();
    }

    @Override
    public Post getById(Long id) {
        return getAll().stream()
                .filter(post -> id.equals(post.getId()))
                .findAny()
                .orElseThrow(() -> new PostNotFoundException(String.format(POST_NOT_FOUND, id)));
    }

    @Override
    public void update(Post post) {
        entityManager.merge(post);
    }

    @Override
    public void deleteById(Long id) {
        entityManager.createQuery(QUERY_DELETE_FROM_POST_BY_POST_ID)
                .setParameter("id", id)
                .executeUpdate();
    }
}
