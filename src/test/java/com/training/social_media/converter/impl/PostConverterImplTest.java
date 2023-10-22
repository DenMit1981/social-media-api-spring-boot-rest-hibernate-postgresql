package com.training.social_media.converter.impl;

import com.training.social_media.dto.PostDto;
import com.training.social_media.model.Post;
import com.training.social_media.model.User;
import com.training.social_media.model.enums.Role;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;

public class PostConverterImplTest {

    private PostConverterImpl postConverter;

    @Before
    public void setUp() throws ParseException {
        postConverter = new PostConverterImpl();
    }

    @Test
    public void convertToPostDtoTest() {
        User user = createTestUser(1L, "Den", "den_mogilev@yopmail.com", "1234", Role.ROLE_REGISTERED_USER);

        Post post = new Post();

        post.setHeader("Post 1");
        post.setText("This is a post 1");
        post.setUser(user);

        PostDto postDto = postConverter.convertToPostDto(post);

        assertEquals(post.getHeader(), postDto.getHeader());
        assertEquals(post.getText(), postDto.getText());
    }

    @Test
    public void fromPostDtoTest() {
        String header = "Post 1";
        String text = "This is a post 1";

        Post post = postConverter.fromPostDto(new PostDto() {{
            setHeader(header);
            setText(text);
        }});

        assertEquals(header, post.getHeader());
        assertEquals(text, post.getText());
    }

    private User createTestUser(Long id, String name, String email, String password, Role role) {
        User user = new User();

        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);

        return user;
    }
}
