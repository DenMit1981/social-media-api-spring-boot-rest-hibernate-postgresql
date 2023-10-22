package com.training.social_media.converter.impl;

import com.training.social_media.converter.PostConverter;
import com.training.social_media.dto.PostDto;
import com.training.social_media.model.Post;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PostConverterImpl implements PostConverter {

    @Override
    public PostDto convertToPostDto(Post post) {
        PostDto postDto = new PostDto();

        postDto.setId(post.getId());
        postDto.setUser(post.getUser().getName());
        postDto.setDate(post.getDate());
        postDto.setText(post.getText());
        postDto.setHeader(post.getHeader());

        return postDto;
    }

    @Override
    public Post fromPostDto(PostDto postDto) {
        Post post = new Post();

        post.setId(postDto.getId());
        post.setHeader(postDto.getHeader());
        post.setText(postDto.getText());
        post.setDate(LocalDateTime.now());

        return post;
    }

    @Override
    public List<PostDto> convertToListPostDto(List<Post> posts) {
        return posts.stream()
                .map(this::convertToPostDto)
                .collect(Collectors.toList());
    }
}
