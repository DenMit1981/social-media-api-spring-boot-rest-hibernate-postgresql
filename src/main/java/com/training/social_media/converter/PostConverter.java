package com.training.social_media.converter;

import com.training.social_media.dto.PostDto;
import com.training.social_media.model.Post;

import java.util.List;

public interface PostConverter {

    PostDto convertToPostDto(Post post);

    Post fromPostDto(PostDto postDto);

    List<PostDto> convertToListPostDto(List<Post> posts);
}
