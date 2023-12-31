package com.training.social_media.controller;

import com.training.social_media.dto.PostDto;
import com.training.social_media.dto.UserLoginDto;
import com.training.social_media.dto.UserRegisterDto;
import com.training.social_media.model.User;
import com.training.social_media.service.PostService;
import com.training.social_media.service.UserService;
import com.training.social_media.service.ValidationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping
@Api("User controller")
public class UserController {

    private final UserService userService;
    private final PostService postService;
    private final ValidationService validationService;

    @PostMapping("/")
    @ApiOperation(value = "Register a new user")
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserRegisterDto userDto,
                                          BindingResult bindingResult) {
        List<String> errorMessage = validationService.generateErrorMessage(bindingResult);

        if (checkErrors(errorMessage)) {
            return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
        }

        User savedUser = userService.save(userDto);

        String currentUri = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        String savedUserLocation = currentUri + "/" + savedUser.getId();

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, savedUserLocation)
                .body(savedUser);
    }

    @PostMapping("/auth")
    @ApiOperation(value = "Authenticate and generate JWT token")
    public ResponseEntity<?> authenticationUser(@RequestBody @Valid UserLoginDto userDto,
                                                BindingResult bindingResult) {
        List<String> errorMessage = validationService.generateErrorMessage(bindingResult);

        if (checkErrors(errorMessage)) {
            return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
        }

        Map<Object, Object> response = userService.authenticateUser(userDto);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/posts")
    @ApiOperation(value = "Get all posts by user id")
    public ResponseEntity<List<PostDto>> getAllPostsByUserId(@PathVariable Long userId) {
        List<PostDto> posts = postService.getAllByUserId(userId);

        return ResponseEntity.ok(posts);
    }

    @GetMapping("users/{userId}/activity-feed")
    @ApiOperation(value = "Get last five posts for user from subscribers by user id")
    public ResponseEntity<List<PostDto>> getActivityFeedForUserByUserId(@PathVariable Long userId,
                                                                        @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                                                                        @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber) {
        List<PostDto> posts = postService.getActivityFeedByUserId(userId, pageSize, pageNumber);

        return ResponseEntity.ok(posts);
    }

    private boolean checkErrors(List<String> errorMessage) {
        return !errorMessage.isEmpty();
    }
}
