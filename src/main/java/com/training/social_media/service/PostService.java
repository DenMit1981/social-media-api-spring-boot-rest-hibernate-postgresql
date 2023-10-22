package com.training.social_media.service;

import com.training.social_media.dto.PostDto;
import com.training.social_media.model.Post;

import java.util.List;

public interface PostService {

    Post save(PostDto postDto, String login);

    List<PostDto> getAll();

    List<PostDto> getAllForCurrentUser(String login);

    List<PostDto> getAllByUserId(Long id);

    List<PostDto> getActivityFeedForCurrentUser(String currentUserLogin, int pageSize, int pageNumber);

    List<PostDto> getActivityFeedByUserId(Long userId, int pageSize, int pageNumber);

    PostDto getById(Long id);

    Post update(Long id, PostDto postDto, String login);

    void deleteById(Long id, String login);
}
