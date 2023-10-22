package com.training.social_media.dao;

import com.training.social_media.model.Image;

import java.util.List;

public interface ImageDAO {

    void save(Image image);

    Image getByImageIdAndPostId(Long imageId, Long postId);

    List<Image> getAllByPostId(Long postId);

    void deleteByImageNameAndPostId(String imageName, Long postId);
}
