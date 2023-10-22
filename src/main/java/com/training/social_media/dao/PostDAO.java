package com.training.social_media.dao;

import com.training.social_media.model.Post;

import java.util.List;

public interface PostDAO {

    void save(Post post);

    List<Post> getAll();

    List<Post> getAllCreatedByCurrentUser(String userLogin);

    Post getById(Long id);

    void update(Post post);

    void deleteById(Long id);
}
