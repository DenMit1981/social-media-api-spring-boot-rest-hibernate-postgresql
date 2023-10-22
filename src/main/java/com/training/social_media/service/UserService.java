package com.training.social_media.service;

import com.training.social_media.dto.UserLoginDto;
import com.training.social_media.dto.UserRegisterDto;
import com.training.social_media.model.User;

import java.util.Map;

public interface UserService {

    User save(UserRegisterDto userDto);

    Map<Object, Object> authenticateUser(UserLoginDto userDto);

    User getByLoginAndPassword(String login, String password);
}
