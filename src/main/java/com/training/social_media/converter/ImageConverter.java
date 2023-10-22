package com.training.social_media.converter;

import com.training.social_media.dto.ImageDto;
import com.training.social_media.model.Image;

public interface ImageConverter {

    ImageDto convertToImageDto(Image image);

    Image fromImageDto(ImageDto imageDto);
}
