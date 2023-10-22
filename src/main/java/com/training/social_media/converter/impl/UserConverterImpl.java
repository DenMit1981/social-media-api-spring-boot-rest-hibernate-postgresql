package com.training.social_media.converter.impl;

import com.training.social_media.converter.UserConverter;
import com.training.social_media.model.User;
import com.training.social_media.dto.UserRegisterDto;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserConverterImpl implements UserConverter {

    private final PasswordEncoder passwordEncoder;

    @Override
    public User fromUserRegisterDto(UserRegisterDto userDto) {
        User user = new User();

        user.setName(userDto.getName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEmail(userDto.getEmail());

        return user;
    }
}
