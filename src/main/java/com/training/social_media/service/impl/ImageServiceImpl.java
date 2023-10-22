package com.training.social_media.service.impl;

import com.training.social_media.converter.ImageConverter;
import com.training.social_media.dao.ImageDAO;
import com.training.social_media.dao.PostDAO;
import com.training.social_media.dto.ImageDto;
import com.training.social_media.exception.ImageNotFoundException;
import com.training.social_media.model.Image;
import com.training.social_media.model.Post;
import com.training.social_media.service.ImageService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ImageServiceImpl implements ImageService {

    private static final Logger LOGGER = LogManager.getLogger(ImageServiceImpl.class.getName());

    private static final String FILE_NOT_FOUND = "This file is absent";

    private final ImageDAO imageDAO;
    private final ImageConverter imageConverter;
    private final PostDAO postDAO;

    @Override
    @Transactional
    public Image save(@NonNull ImageDto imageDto, Long postId) {
        Image image = imageConverter.fromImageDto(imageDto);

        Post post = postDAO.getById(postId);

        image.setPost(post);

        imageDAO.save(image);

        LOGGER.info("New file {} has just been added to post {}", imageDto.getName(), postId);

        return image;
    }

    @Override
    @Transactional
    public void deleteByName(String fileName, Long postId) {
        if (isFilePresent(fileName, postId)) {
            imageDAO.deleteByImageNameAndPostId(fileName, postId);

            LOGGER.info("File {} has just been deleted from post {}", fileName, postId);
        } else {
            LOGGER.error("File {} is absent in post {}", fileName, postId);

            throw new ImageNotFoundException(FILE_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public List<ImageDto> getAllByPostId(Long postId) {
        List<Image> images = imageDAO.getAllByPostId(postId);

        LOGGER.info("All files for post {} : {}", postId, images);

        return images.stream()
                .map(imageConverter::convertToImageDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ImageDto getById(Long imageId, Long postId) {
        Image image = imageDAO.getByImageIdAndPostId(imageId, postId);

        LOGGER.info("File {} is downloaded", image.getName());

        return imageConverter.convertToImageDto(image);
    }

    @Override
    public ImageDto getChosenImage(@NonNull MultipartFile file) throws IOException {
        ImageDto imageDto = new ImageDto();

        imageDto.setName(file.getOriginalFilename());
        imageDto.setFile(file.getBytes());

        return imageDto;
    }

    private boolean isFilePresent(String fileName, Long postId) {
        return imageDAO.getAllByPostId(postId).stream().anyMatch(file -> fileName.equals(file.getName()));
    }
}

