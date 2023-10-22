package com.training.social_media.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.social_media.dto.PostDto;
import com.training.social_media.dto.UserRegisterDto;
import com.training.social_media.model.Post;
import com.training.social_media.model.User;
import com.training.social_media.service.PostService;
import com.training.social_media.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String[] CLEAN_TABLES_SQL = {
            "delete from posts",
            "delete from users"
    };

    @AfterEach
    public void resetDb() {
        for (String query : CLEAN_TABLES_SQL) {
            jdbcTemplate.execute(query);
        }
    }

    @Test
    @WithMockUser(username = "den1_mogilev@yopmail.com", password = "1234")
    void givenPost_whenCreateValidPost_thenStatus201andPostReturned() throws Exception {
        User user = createTestUser("Denis", "den1_mogilev@yopmail.com", "1234");

        PostDto newPost = new PostDto();

        newPost.setHeader("Post 1");
        newPost.setText("This is a new post");

        mockMvc.perform(
                        post("http://localhost:8081/posts")
                                .content(objectMapper.writeValueAsString(newPost))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.header").value("Post 1"))
                .andExpect(jsonPath("$.text").value("This is a new post"));
    }

    @Test
    @WithMockUser(username = "den1_mogilev@yopmail.com", password = "1234")
    void givenError_whenCreateInvalidPostWithEmptyHeader_thenStatus400BadRequest() throws Exception {
        PostDto newPost = new PostDto();

        newPost.setHeader("");
        newPost.setText("This is a new post");

        mockMvc.perform(
                        post("http://localhost:8081/posts")
                                .content(objectMapper.writeValueAsString(newPost))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "jimmy1_mogilev@yopmail.com", password = "P@ssword1")
    void givenId_whenGetExistingPost_thenStatus200andPostReturned() throws Exception {
        User user = createTestUser("Jimmy1", "jimmy1_mogilev@yopmail.com", "P@ssword1");
        long id = createTestPost("Post 1", "This is a post",
                user.getEmail()).getId();

        mockMvc.perform(
                        get("http://localhost:8081/posts/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.header").value("Post 1"))
                .andExpect(jsonPath("$.text").value("This is a post"));

    }

    @Test
    @WithMockUser(username = "den1_mogilev@yopmail.com", password = "1234")
    void givenError_whenGetNotExistingPost_thenStatus404NotFound() throws Exception {
        User user = createTestUser("Denis", "den1_mogilev@yopmail.com", "1234");

        long postId = createTestPost("Post 1", "This is a post",
                user.getEmail()).getId();

        String error = "Post with id " + (postId + 1) + " not found";

        mockMvc.perform(
                        get("http://localhost:8081/posts/{id}", postId + 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.info").value(error));
    }

    @Test
    @WithMockUser(username = "den1_mogilev@yopmail.com", password = "1234")
    void givenPost_whenUpdate_thenStatus200andUpdatedPostReturned() throws Exception {
        User user = createTestUser("Denis", "den1_mogilev@yopmail.com", "1234");

        long postId = createTestPost("Post 1", "This is a post",
                user.getEmail()).getId();

        PostDto updatedPost = new PostDto();

        updatedPost.setHeader("Post 2");
        updatedPost.setText("This is an updated post");

        mockMvc.perform(
                        put("http://localhost:8081/posts/{id}", postId)
                                .content(objectMapper.writeValueAsString(updatedPost))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.header").value("Post 2"))
                .andExpect(jsonPath("$.text").value("This is an updated post"));
    }

    @Test
    @WithMockUser(username = "asya1_mogilev@yopmail.com", password = "5678")
    void givenError_whenUpdatePostByAnotherUser_thenStatus403Forbidden() throws Exception {
        User user1 = createTestUser("Denis", "den1_mogilev@yopmail.com", "1234");
        User user2 = createTestUser("Asya1", "asya1_mogilev@yopmail.com", "5678");

        long postId = createTestPost("Post 1", "This is a post",
                user1.getEmail()).getId();

        PostDto updatedPost = new PostDto();

        updatedPost.setHeader("Post 2");
        updatedPost.setText("This is an updated post");

        mockMvc.perform(
                        put("http://localhost:8081/posts/{id}", postId)
                                .content(objectMapper.writeValueAsString(updatedPost))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.info").value("User can update or delete only own posts"));
    }

    @Test
    @WithMockUser(username = "den1_mogilev@yopmail.com", password = "1234")
    void givenAllPosts_thenStatus200andListOfPostsReturned() throws Exception {
        List<PostDto> posts = postService.getAll();

        mockMvc.perform(
                        get("http://localhost:8081/posts"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(posts)));
    }

    @Test
    @WithMockUser(username = "den1_mogilev@yopmail.com", password = "1234")
    void givenAllPostsForCurrentUser_thenStatus200andListOfPostsReturned() throws Exception {
        List<PostDto> posts = postService.getAllForCurrentUser("den1_mogilev@yopmail.com");

        mockMvc.perform(
                        get("http://localhost:8081/posts/my-posts"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(posts)));
    }

    @Test
    @WithMockUser(username = "den1_mogilev@yopmail.com", password = "1234")
    void givenActivityFeedForCurrentUser_thenStatus200andListOfPostsReturned() throws Exception {
        User user = createTestUser("Denis", "den1_mogilev@yopmail.com", "1234");

        List<PostDto> posts = postService.getActivityFeedForCurrentUser(user.getEmail(), 5, 1);

        mockMvc.perform(
                        get("http://localhost:8081/posts/my-activity-feed"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(posts)));
    }

    @Test
    @WithMockUser(username = "den1_mogilev@yopmail.com", password = "1234")
    public void deletePost_whenDeletePostById_thenStatus200() throws Exception {
        User user = createTestUser("Denis", "den1_mogilev@yopmail.com", "1234");

        Post post = createTestPost("Post 1", "This is a post",
                user.getEmail());

        mockMvc.perform(
                        delete("http://localhost:8081/posts/{id}", post.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "asya1_mogilev@yopmail.com", password = "5678")
    public void givenError_whenDeletePostByAnotherUser_thenStatus403Forbidden() throws Exception {
        User user = createTestUser("Denis", "den1_mogilev@yopmail.com", "1234");

        Post post = createTestPost("Post 1", "This is a post",
                user.getEmail());

        mockMvc.perform(
                        delete("http://localhost:8081/posts/{id}", post.getId()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.info").value("User can update or delete only own posts"));
    }

    private Post createTestPost(String header, String text, String login) {
        PostDto postDto = new PostDto();

        postDto.setHeader(header);
        postDto.setText(text);

        return postService.save(postDto, login);
    }

    private User createTestUser(String name, String email, String password) {
        UserRegisterDto userRegisterDto = new UserRegisterDto();

        userRegisterDto.setName(name);
        userRegisterDto.setEmail(email);
        userRegisterDto.setPassword(password);

        return userService.save(userRegisterDto);
    }
}
