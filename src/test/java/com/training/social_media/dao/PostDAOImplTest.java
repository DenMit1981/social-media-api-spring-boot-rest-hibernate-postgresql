package com.training.social_media.dao;

import com.training.social_media.exception.PostNotFoundException;
import com.training.social_media.model.Post;
import com.training.social_media.model.User;
import com.training.social_media.model.enums.Role;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class PostDAOImplTest {

    @Autowired
    private PostDAO postDAO;

    @Autowired
    private UserDAO userDAO;

    @Test
    public void givenPost_whenSave_thenReturnSavedPost() {
        Post post = createTestPost("Post 1", "This is a post 1");

        postDAO.save(post);

        assertThat(post).isNotNull();
        assertThat(post.getId()).isGreaterThan(0);
    }

    @Test
    public void givenPostList_whenGetAll_thenPostList() {
        Post post1 = createTestPost("Post 1", "This is a post 1");
        Post post2 = createTestPost("Post 2", "This is a post 2");

        postDAO.save(post1);
        postDAO.save(post2);

        List<Post> goods = postDAO.getAll();

        assertThat(goods).isNotNull();
        assertThat(goods.size()).isEqualTo(2);
    }

    @Test
    public void givenPostList_whenGetAllCreatedByCurrentUser_thenPostList() {
        User user1 = createTestUser(1L, "Denis", "den1_mogilev@yopmail.com", "1234", Role.ROLE_REGISTERED_USER);
        User user2 = createTestUser(2L, "Asya1", "asya1_mogilev@yopmail.com", "5678", Role.ROLE_REGISTERED_USER);

        userDAO.update(user1);
        userDAO.update(user2);

        Post post1 = createTestPost("Post 1", "This is a post 1");
        post1.setUser(user1);
        Post post2 = createTestPost("Post 2", "This is a post 2");
        post2.setUser(user2);
        Post post3 = createTestPost("Post 3", "This is a post 3");
        post3.setUser(user1);

        postDAO.save(post1);
        postDAO.save(post2);
        postDAO.save(post3);

        List<Post> goods = postDAO.getAllCreatedByCurrentUser(user1.getEmail());

        assertThat(goods).isNotNull();
        assertThat(goods.size()).isEqualTo(2);
    }

    @Test
    public void givenPost_whenGetById_thenReturnPost() {
        Post post = createTestPost("Post 1", "This is a post 1");
        postDAO.save(post);

        Post searchedPost = postDAO.getById(post.getId());

        assertThat(searchedPost).isNotNull();
    }

    @Test(expected = PostNotFoundException.class)
    public void givenPost_whenFindByIdButDoesNotExist_thenReturnEmptiness() {
        Post post = createTestPost("Post 1", "This is a post 1");
        postDAO.save(post);

        Post searchedPost = postDAO.getById(post.getId() + 1);

        assertThat(searchedPost).isNull();
    }

    @Test
    public void givenPost_whenUpdatePost_thenReturnUpdatedPost() {
        Post post = createTestPost("Post 1", "This is a post 1");
        postDAO.save(post);

        Post savedPost = postDAO.getById(post.getId());

        savedPost.setHeader("New Post");
        savedPost.setText("It's updated post");

        postDAO.update(savedPost);

        assertThat(savedPost.getHeader()).isEqualTo("New Post");
        assertThat(savedPost.getText()).isEqualTo("It's updated post");
    }

    @Test(expected = PostNotFoundException.class)
    public void givenPost_whenDelete_thenRemovePost() {
        Post post = createTestPost("Post 1", "This is a post 1");

        postDAO.save(post);

        postDAO.deleteById(post.getId());

        Post removedPost = postDAO.getById(post.getId());

        assertThat(removedPost).isNull();
    }

    private Post createTestPost(String header, String text) {
        Post post = new Post();

        post.setHeader(header);
        post.setText(text);

        return post;
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
