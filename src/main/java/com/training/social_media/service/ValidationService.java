package com.training.social_media.service;

import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ValidationService {

    List<String> generateErrorMessage(BindingResult bindingResult);

    List<String> validateUploadFile(MultipartFile file);
}
