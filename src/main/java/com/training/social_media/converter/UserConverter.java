package com.training.social_media.converter;

import com.training.social_media.model.User;
import com.training.social_media.dto.UserRegisterDto;

public interface UserConverter {

    User fromUserRegisterDto(UserRegisterDto userDto);
}
