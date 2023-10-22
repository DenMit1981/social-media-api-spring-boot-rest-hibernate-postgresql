package com.training.social_media.service;

import com.training.social_media.dto.ImageDto;
import com.training.social_media.model.Image;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {

    List<ImageDto> getAllByPostId(Long postId);

    ImageDto getById(Long imageId, Long postId);

    ImageDto getChosenImage(MultipartFile file) throws IOException;

    Image save(@NonNull ImageDto imageDto, Long postId);

    void deleteByName(String name, Long postId);
}
