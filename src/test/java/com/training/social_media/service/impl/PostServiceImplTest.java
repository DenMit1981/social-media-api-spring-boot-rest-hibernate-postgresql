package com.training.social_media.service.impl;

import com.training.social_media.converter.PostConverter;
import com.training.social_media.dao.PostDAO;
import com.training.social_media.dao.UserDAO;
import com.training.social_media.dto.PostDto;
import com.training.social_media.exception.AccessDeniedException;

import com.training.social_media.model.Post;
import com.training.social_media.model.User;
import com.training.social_media.model.enums.Role;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PostServiceImplTest {

    private static final String ACCESS_DENIED_FOR_UPDATE_AND_DELETE_POST = "User can update or delete only own posts";

    @Mock
    private PostDAO postDAO;

    @Mock
    private UserDAO userDAO;

    @Mock
    private PostConverter postConverter;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    public void getAllPostsTest() {
        User userOne = createTestUser(1L, "Den", "den_mogilev@yopmail.com", "1234", Role.ROLE_REGISTERED_USER);
        User userTwo = createTestUser(2L, "Jimmy", "jimmy_mogilev@yopmail.com", "1111", Role.ROLE_REGISTERED_USER);

        Post postOne = createTestPost(1L, "Post 1", "This is a post 1", LocalDateTime.now(), userOne);
        Post postTwo = createTestPost(2L, "Post 2", "This is a post 2", LocalDateTime.parse(("2023-04-11T09:12:30")), userTwo);
        Post postThree = createTestPost(3L, "Post 3", "This is a post 3", LocalDateTime.parse(("2023-08-11T11:10:35")), userTwo);
        Post postFour = createTestPost(4L, "Post 4", "This is a post 4", LocalDateTime.parse(("2023-01-11T09:12:30")), userOne);

        List<Post> posts = asList(postOne, postTwo, postThree, postFour);

        List<PostDto> expected = posts
                .stream()
                .map(postConverter::convertToPostDto)
                .collect(Collectors.toList());

        when(postDAO.getAll()).thenReturn(posts);
        when(postConverter.convertToListPostDto(posts)).thenReturn(expected);

        List<PostDto> actual = postService.getAll();

        Assert.assertEquals(4, actual.size());
        Assert.assertEquals(4, expected.size());
        Assert.assertEquals(expected, actual);

        verify(postDAO, times(1)).getAll();
        verify(postConverter, times(1)).convertToListPostDto(posts);
    }

    @Test
    public void getAllGetAllPostsForCurrentUserTest() {
        User userOne = createTestUser(1L, "Den", "den_mogilev@yopmail.com", "1234", Role.ROLE_REGISTERED_USER);
        User userTwo = createTestUser(2L, "Jimmy", "jimmy_mogilev@yopmail.com", "1111", Role.ROLE_REGISTERED_USER);

        Post postOne = createTestPost(1L, "Post 1", "This is a post 1", LocalDateTime.now(), userOne);
        Post postTwo = createTestPost(2L, "Post 2", "This is a post 2", LocalDateTime.parse(("2023-04-11T09:12:30")), userTwo);
        Post postThree = createTestPost(3L, "Post 3", "This is a post 3", LocalDateTime.parse(("2023-08-11T11:10:35")), userTwo);
        Post postFour = createTestPost(4L, "Post 4", "This is a post 4", LocalDateTime.parse(("2023-01-11T09:12:30")), userOne);

        List<Post> posts = Stream.of(postOne, postTwo, postThree, postFour)
                .filter(post -> post.getUser().equals(userOne))
                .collect(Collectors.toList());

        List<PostDto> expected = posts
                .stream()
                .map(postConverter::convertToPostDto)
                .collect(Collectors.toList());

        when(postDAO.getAllCreatedByCurrentUser(userOne.getEmail())).thenReturn(posts);
        when(postConverter.convertToListPostDto(posts)).thenReturn(expected);

        List<PostDto> actual = postService.getAllForCurrentUser(userOne.getEmail());

        Assert.assertEquals(2, actual.size());
        Assert.assertEquals(2, expected.size());
        Assert.assertEquals(expected, actual);

        verify(postDAO, times(1)).getAllCreatedByCurrentUser(userOne.getEmail());
        verify(postConverter, times(1)).convertToListPostDto(posts);
    }

    @Test
    public void getAllGetAllPostsByUserIdTest() {
        User userOne = createTestUser(1L, "Den", "den_mogilev@yopmail.com", "1234", Role.ROLE_REGISTERED_USER);
        User userTwo = createTestUser(2L, "Jimmy1", "jimmy1_mogilev@yopmail.com", "1111", Role.ROLE_REGISTERED_USER);

        Post postOne = createTestPost(1L, "Post 1", "This is a post 1", LocalDateTime.now(), userOne);
        Post postTwo = createTestPost(2L, "Post 2", "This is a post 2", LocalDateTime.parse(("2023-04-11T09:12:30")), userTwo);
        Post postThree = createTestPost(3L, "Post 3", "This is a post 3", LocalDateTime.parse(("2023-08-11T11:10:35")), userOne);
        Post postFour = createTestPost(4L, "Post 4", "This is a post 4", LocalDateTime.parse(("2023-01-11T09:12:30")), userOne);

        List<Post> posts = Stream.of(postOne, postTwo, postThree, postFour)
                .filter(post -> Objects.equals(post.getUser().getId(), userTwo.getId()))
                .collect(Collectors.toList());

        List<PostDto> expected = posts
                .stream()
                .map(postConverter::convertToPostDto)
                .collect(Collectors.toList());

        when(postDAO.getAll()).thenReturn(List.of(postOne, postTwo, postThree, postFour));
        when(postConverter.convertToListPostDto(posts)).thenReturn(expected);

        List<PostDto> actual = postService.getAllByUserId(userTwo.getId());

        Assert.assertEquals(1, actual.size());
        Assert.assertEquals(1, expected.size());
        Assert.assertEquals(expected, actual);

        verify(postDAO, times(1)).getAll();
        verify(postConverter, times(1)).convertToListPostDto(posts);
    }

    @Test
    public void getAllGetActivityFeedForCurrentUserTest() {
        User userOne = createTestUser(1L, "Den", "den_mogilev@yopmail.com", "1234", Role.ROLE_REGISTERED_USER);
        User userTwo = createTestUser(2L, "Jimmy", "jimmy_mogilev@yopmail.com", "1111", Role.ROLE_REGISTERED_USER);
        User userThree = createTestUser(3L, "Asya", "asya_mogilev@yopmail.com", "5678", Role.ROLE_REGISTERED_USER);
        User userFour = createTestUser(4L, "Peter", "peter_mogilev@yopmail.com", "4321", Role.ROLE_REGISTERED_USER);

        Set<User> subscribers = new HashSet<>();
        subscribers.add(userTwo);
        subscribers.add(userThree);

        userOne.setSubscribers(subscribers);

        Post postOne = createTestPost(1L, "Post 1", "This is a post 1", LocalDateTime.now(), userOne);
        Post postTwo = createTestPost(2L, "Post 2", "This is a post 2", LocalDateTime.parse(("2023-04-11T09:12:30")), userTwo);
        Post postThree = createTestPost(3L, "Post 3", "This is a post 3", LocalDateTime.parse(("2023-08-11T11:10:35")), userTwo);
        Post postFour = createTestPost(4L, "Post 4", "This is a post 4", LocalDateTime.parse(("2023-01-11T09:12:30")), userThree);
        Post postFive = createTestPost(4L, "Post 4", "This is a post 5", LocalDateTime.now(), userFour);

        List<Post> posts = Stream.of(postOne, postTwo, postThree, postFour, postFive)
                .filter(post -> subscribers.contains(post.getUser()))
                .sorted(Comparator.comparing(Post::getDate).reversed())
                .limit(5)
                .collect(Collectors.toList());

        List<PostDto> expected = posts.stream()
                .map(postConverter::convertToPostDto)
                .collect(Collectors.toList());

        when(postDAO.getAll()).thenReturn(posts);
        when(userDAO.getByLogin(userOne.getEmail())).thenReturn(userOne);
        when(userDAO.getById(userOne.getId())).thenReturn(userOne);
        when(postConverter.convertToListPostDto(posts)).thenReturn(expected);

        List<PostDto> actual = postService.getActivityFeedForCurrentUser(userOne.getEmail(), 5, 1);

        Assert.assertEquals(3, actual.size());
        Assert.assertEquals(3, expected.size());
        Assert.assertEquals(expected, actual);

        verify(postDAO, times(1)).getAll();
        verify(postConverter, times(1)).convertToListPostDto(posts);
        verify(userDAO, times(1)).getByLogin(userOne.getEmail());
        verify(userDAO, times(1)).getById(userOne.getId());
    }

    @Test
    public void getActivityFeedByUserIdTest() {
        User userOne = createTestUser(1L, "Den", "den_mogilev@yopmail.com", "1234", Role.ROLE_REGISTERED_USER);
        User userTwo = createTestUser(2L, "Jimmy", "jimmy_mogilev@yopmail.com", "1111", Role.ROLE_REGISTERED_USER);
        User userThree = createTestUser(3L, "Asya", "asya_mogilev@yopmail.com", "5678", Role.ROLE_REGISTERED_USER);
        User userFour = createTestUser(4L, "Peter", "peter_mogilev@yopmail.com", "4321", Role.ROLE_REGISTERED_USER);

        Set<User> subscribers = new HashSet<>();
        subscribers.add(userThree);
        subscribers.add(userFour);

        userOne.setSubscribers(subscribers);

        Post postOne = createTestPost(1L, "Post 1", "This is a post 1", LocalDateTime.now(), userOne);
        Post postTwo = createTestPost(2L, "Post 2", "This is a post 2", LocalDateTime.parse(("2023-04-11T09:12:30")), userTwo);
        Post postThree = createTestPost(3L, "Post 3", "This is a post 3", LocalDateTime.parse(("2023-08-11T11:10:35")), userTwo);
        Post postFour = createTestPost(4L, "Post 4", "This is a post 4", LocalDateTime.parse(("2023-01-11T09:12:30")), userThree);
        Post postFive = createTestPost(4L, "Post 4", "This is a post 5", LocalDateTime.now(), userFour);

        List<Post> posts = Stream.of(postOne, postTwo, postThree, postFour, postFive)
                .filter(post -> subscribers.contains(post.getUser()))
                .sorted(Comparator.comparing(Post::getDate).reversed())
                .limit(5)
                .collect(Collectors.toList());

        List<PostDto> expected = posts.stream()
                .map(postConverter::convertToPostDto)
                .collect(Collectors.toList());

        when(postDAO.getAll()).thenReturn(List.of(postOne, postTwo, postThree, postFour, postFive));
        when(userDAO.getById(userOne.getId())).thenReturn(userOne);
        when(postConverter.convertToListPostDto(posts)).thenReturn(expected);

        List<PostDto> actual = postService.getActivityFeedByUserId(userOne.getId(), 5, 1);

        Assert.assertEquals(2, actual.size());
        Assert.assertEquals(2, expected.size());
        Assert.assertEquals(expected, actual);

        verify(postDAO, times(1)).getAll();
        verify(postConverter, times(1)).convertToListPostDto(posts);
        verify(userDAO, times(1)).getById(userOne.getId());
    }

    @Test
    public void saveNewPostTest() {
        User user = createTestUser(1L, "Den", "den_mogilev@yopmail.com", "1234", Role.ROLE_REGISTERED_USER);

        when(userDAO.getByLogin(user.getEmail())).thenReturn(user);

        PostDto postDto = createTestPostDto("Post 1", "This is a post");

        Post post = new Post();

        post.setHeader(postDto.getHeader());
        post.setText(postDto.getText());

        when(postConverter.fromPostDto(postDto)).thenReturn(post);

        Post result = postService.save(postDto, user.getEmail());

        Assert.assertNotNull(result);
        Assert.assertEquals(post.getHeader(), result.getHeader());

        verify(postDAO, times(1)).save(postConverter.fromPostDto(postDto));
        verify(userDAO, times(1)).getByLogin(user.getEmail());
    }

    @Test
    public void getPostByIdTest_ThenReturnPost() {
        User user = createTestUser(1L, "Den", "den_mogilev@yopmail.com", "1234", Role.ROLE_REGISTERED_USER);

        Post post = createTestPost(1L, "Post 1", "This is a post 1", LocalDateTime.now(), user);

        when(postDAO.getById(post.getId())).thenReturn(post);

        PostDto expected = postConverter.convertToPostDto(post);

        PostDto actual = postService.getById(post.getId());

        Assert.assertEquals(expected, actual);

        verify(postDAO, times(1)).getById(post.getId());
    }

    @Test
    public void updatePostTest() {
        User user = createTestUser(1L, "Den", "den_mogilev@yopmail.com", "1234", Role.ROLE_REGISTERED_USER);

        Post post = createTestPost(1L, "Post 1", "This is a post 1", LocalDateTime.now(), user);

        when(userDAO.getByLogin(user.getEmail())).thenReturn(user);

        PostDto postDto = new PostDto();

        when(postConverter.fromPostDto(postDto)).thenReturn(post);
        when(postDAO.getAllCreatedByCurrentUser(user.getEmail())).thenReturn((List.of(post)));

        Post result = postService.update(post.getId(), postDto, user.getEmail());

        Assert.assertNotNull(result);
        Assert.assertEquals(post, result);

        verify(postDAO, times(1)).update(postConverter.fromPostDto(postDto));
        verify(userDAO, times(1)).getByLogin(user.getEmail());
        verify(postDAO, times(1)).getAllCreatedByCurrentUser(user.getEmail());
    }

    @Test
    public void updatePostNegativeTest_whenUpdatePostByAnotherUser_ThenStatus403Forbidden() {
        User user1 = createTestUser(1L, "Den", "den_mogilev@yopmail.com", "1234", Role.ROLE_REGISTERED_USER);
        User user2 = createTestUser(2L, "Asya", "asya_mogilev@yopmail.com", "5678", Role.ROLE_REGISTERED_USER);

        Post post = createTestPost(1L, "Post 1", "This is a post 1", LocalDateTime.now(), user1);

        when(userDAO.getByLogin(user2.getEmail())).thenReturn(user2);

        PostDto postDto = new PostDto();

        when(postConverter.fromPostDto(postDto)).thenReturn(post);

        assertThrows(AccessDeniedException.class,
                () -> postService.update(1L, postDto, user2.getEmail()));

        try {
            postService.update(1L, postDto, user2.getEmail());
        } catch (AccessDeniedException e) {
            if (e.getMessage().equals(ACCESS_DENIED_FOR_UPDATE_AND_DELETE_POST)) {
                return;
            }
        }
        Assert.fail();
    }

    @Test
    public void givenPostId_whenDeletePost_thenNothing() {
        User user = createTestUser(1L, "Den", "den_mogilev@yopmail.com", "1234", Role.ROLE_REGISTERED_USER);

        Post post = createTestPost(1L, "Post 1", "This is a post 1", LocalDateTime.now(), user);

        when(postDAO.getAllCreatedByCurrentUser(user.getEmail())).thenReturn((List.of(post)));

        willDoNothing().given(postDAO).deleteById(post.getId());

        postService.deleteById(post.getId(), user.getEmail());

        verify(postDAO, times(1)).deleteById(post.getId());
        verify(postDAO, times(1)).getAllCreatedByCurrentUser(user.getEmail());
    }

    @Test
    public void deletePostNegativeTest_whenDeletePostByAnotherUser_ThenStatus403Forbidden() {
        User user1 = createTestUser(1L, "Den", "den_mogilev@yopmail.com", "1234", Role.ROLE_REGISTERED_USER);
        User user2 = createTestUser(2L, "Asya", "asya_mogilev@yopmail.com", "5678", Role.ROLE_REGISTERED_USER);

        Post post = createTestPost(1L, "Post 1", "This is a post 1", LocalDateTime.now(), user1);

        assertThrows(AccessDeniedException.class,
                () -> postService.deleteById(post.getId(), user2.getEmail()));

        try {
            postService.deleteById(post.getId(), user2.getEmail());
        } catch (AccessDeniedException e) {
            if (e.getMessage().equals(ACCESS_DENIED_FOR_UPDATE_AND_DELETE_POST)) {
                return;
            }
        }
        Assert.fail();
    }

    private Post createTestPost(Long id, String header, String text, LocalDateTime date, User user) {
        Post post = new Post();

        post.setId(id);
        post.setHeader(header);
        post.setText(text);
        post.setDate(date);
        post.setUser(user);

        return post;
    }

    private PostDto createTestPostDto(String header, String text) {
        PostDto postDto = new PostDto();

        postDto.setHeader(header);
        postDto.setText(text);

        return postDto;
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
