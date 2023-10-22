package com.training.social_media.converter.impl;

import com.training.social_media.converter.ImageConverter;
import com.training.social_media.dto.ImageDto;
import com.training.social_media.model.Image;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ImageConverterImpl implements ImageConverter {

    @Override
    public ImageDto convertToImageDto(Image image) {
        ImageDto imageDto = new ImageDto();

        imageDto.setId(image.getId());
        imageDto.setName(image.getName());
        imageDto.setFile(image.getFile());

        return imageDto;
    }

    @Override
    public Image fromImageDto(ImageDto imageDto) {
        Image image = new Image();

        image.setId(imageDto.getId());
        image.setName(imageDto.getName());
        image.setFile(imageDto.getFile());

        return image;
    }
}
