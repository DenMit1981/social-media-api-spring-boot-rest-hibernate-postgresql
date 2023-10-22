package com.training.social_media.controller;

import com.training.social_media.dto.ImageDto;
import com.training.social_media.service.ImageService;
import com.training.social_media.service.ValidationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/posts/{postId}/images")
@Api("Image controller")
public class ImageController {

    private final ImageService imageService;
    private final ValidationService validationService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = "Upload image file", authorizations = @Authorization(value = "Bearer"))
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @PathVariable("postId") Long postId) throws IOException {
        List<String> fileUploadErrors = validationService.validateUploadFile(file);

        if (checkErrors(fileUploadErrors)) {
            return new ResponseEntity<>(fileUploadErrors, HttpStatus.BAD_REQUEST);
        }

        ImageDto imageDto = imageService.getChosenImage(file);

        imageService.save(imageDto, postId);

        return new ResponseEntity<>(imageService.getAllByPostId(postId), HttpStatus.OK);
    }

    @GetMapping("/{imageId}")
    @ApiOperation(value = "Get image by id", authorizations = @Authorization(value = "Bearer"))
    public ResponseEntity<ImageDto> getById(@PathVariable("imageId") Long imageId,
                                            @PathVariable("postId") Long postId,
                                            HttpServletResponse response) throws IOException {
        ImageDto imageDto = imageService.getById(imageId, postId);

        response.setContentType("application/octet-stream");

        String headerKey = "Content-Disposition";
        String headerValue = "image; filename = " + imageDto.getName();

        response.setHeader(headerKey, headerValue);

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            outputStream.write(imageDto.getFile());
        }

        return ResponseEntity.ok(imageDto);
    }

    @GetMapping
    @ApiOperation(value = "Get all images by post id", authorizations = @Authorization(value = "Bearer"))
    public ResponseEntity<List<ImageDto>> getAllByPostId(@PathVariable("postId") Long postId) {
        return ResponseEntity.ok(imageService.getAllByPostId(postId));
    }

    @DeleteMapping("/{imageName}")
    @ApiOperation(value = "Delete image by image name", authorizations = @Authorization(value = "Bearer"))
    public ResponseEntity<?> deleteFile(@PathVariable("imageName") String imageName,
                                        @PathVariable("postId") Long postId) {
        imageService.deleteByName(imageName, postId);

        return new ResponseEntity<>(imageService.getAllByPostId(postId), HttpStatus.OK);
    }

    private boolean checkErrors(List<String> fileUploadErrors) {
        return !fileUploadErrors.isEmpty();
    }
}
