package com.training.social_media.controller;

import com.training.social_media.dto.PostDto;
import com.training.social_media.model.Post;
import com.training.social_media.service.PostService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/posts")
@Api("Post controller")
public class PostController {

    private final PostService postService;

    @PostMapping
    @ApiOperation(value = "Create post", authorizations = @Authorization(value = "Bearer"))
    public ResponseEntity<?> save(@RequestBody @Valid PostDto postDto, Principal principal) {
        Post savedPost = postService.save(postDto, principal.getName());

        String currentUri = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        String savedPostLocation = currentUri + "/" + savedPost.getId();

        return ResponseEntity.status(CREATED)
                .header(HttpHeaders.LOCATION, savedPostLocation)
                .body(savedPost);
    }

    @GetMapping
    @ApiOperation(value = "Get all posts", authorizations = @Authorization(value = "Bearer"))
    public ResponseEntity<List<PostDto>> getAll() {
        return ResponseEntity.ok(postService.getAll());
    }

    @GetMapping("/my-posts")
    @ApiOperation(value = "Get all post created by current user", authorizations = @Authorization(value = "Bearer"))
    public ResponseEntity<List<PostDto>> getAllForCurrentUser(Principal principal) {
        List<PostDto> posts = postService.getAllForCurrentUser(principal.getName());

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/my-activity-feed")
    @ApiOperation(value = "Get last five posts for current user from subscribers", authorizations = @Authorization(value = "Bearer"))
    public ResponseEntity<List<PostDto>> getActivityFeedForCurrentUser(Principal principal,
                                                                       @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                                                                       @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber) {
        List<PostDto> posts = postService.getActivityFeedForCurrentUser(principal.getName(), pageSize, pageNumber);

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get post by ID", authorizations = @Authorization(value = "Bearer"))
    public ResponseEntity<PostDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(postService.getById(id));
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "update post by id", authorizations = @Authorization(value = "Bearer"))
    public ResponseEntity<?> update(Principal principal, @PathVariable("id") Long postId,
                                    @RequestBody @Valid PostDto postDto) {
        Post updatedPost = postService.update(postId, postDto, principal.getName());

        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete post by id", authorizations = @Authorization(value = "Bearer"))
    public ResponseEntity<Void> delete(@PathVariable("id") Long postId, Principal principal) {
        postService.deleteById(postId, principal.getName());

        return ResponseEntity.ok().build();
    }
}
