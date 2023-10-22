package com.training.social_media.service.impl;

import com.training.social_media.converter.PostConverter;
import com.training.social_media.dao.PostDAO;
import com.training.social_media.dao.UserDAO;
import com.training.social_media.dto.PostDto;
import com.training.social_media.exception.AccessDeniedException;
import com.training.social_media.model.Post;
import com.training.social_media.model.User;
import com.training.social_media.service.PostService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {

    private static final Logger LOGGER = LogManager.getLogger(PostServiceImpl.class.getName());
    private static final int LAST_FIVE_POSTS = 5;

    private final PostConverter postConverter;
    private final UserDAO userDAO;
    private final PostDAO postDAO;

    @Override
    @Transactional
    public Post save(PostDto postDto, String login) {
        Post post = postConverter.fromPostDto(postDto);

        User user = userDAO.getByLogin(login);

        post.setUser(user);

        postDAO.save(post);

        return post;
    }

    @Override
    @Transactional
    public List<PostDto> getAll() {
        return postConverter.convertToListPostDto(postDAO.getAll());
    }

    @Override
    @Transactional
    public List<PostDto> getAllForCurrentUser(String login) {
        return postConverter.convertToListPostDto(postDAO.getAllCreatedByCurrentUser(login));
    }

    @Override
    @Transactional
    public List<PostDto> getAllByUserId(Long userId) {
        List<Post> posts = postDAO.getAll().stream()
                .filter(post -> post.getUser().getId().equals(userId))
                .collect(Collectors.toList());

        return postConverter.convertToListPostDto(posts);
    }

    @Override
    @Transactional
    public List<PostDto> getActivityFeedForCurrentUser(String currentUserLogin, int pageSize, int pageNumber) {
        User currentUser = userDAO.getByLogin(currentUserLogin);

        List<Post> posts = postDAO.getAll();

        return getLastFiveActivityFeedSortedByDateWithPages(posts, currentUser.getId(), pageSize, pageNumber);
    }

    @Override
    @Transactional
    public List<PostDto> getActivityFeedByUserId(Long userId, int pageSize, int pageNumber) {
        List<Post> posts = postDAO.getAll();

        return getLastFiveActivityFeedSortedByDateWithPages(posts, userId, pageSize, pageNumber);
    }

    @Override
    @Transactional
    public PostDto getById(Long id) {
        Post post = postDAO.getById(id);

        return postConverter.convertToPostDto(post);
    }

    @Override
    @Transactional
    public Post update(Long id, PostDto postDto, String login) {
        Post post = postConverter.fromPostDto(postDto);

        User user = userDAO.getByLogin(login);

        post.setUser(user);
        post.setId(id);

        checkAccess(id, login);

        postDAO.update(post);

        LOGGER.info("Updated post: {}", post);

        return post;
    }

    @Override
    @Transactional
    public void deleteById(Long id, String login) {
        checkAccess(id, login);

        postDAO.deleteById(id);

        LOGGER.info("Posts after removing post with id = {} : {}", id, postDAO.getAll());
    }

    private void checkAccess(Long id, String login) {
        if (!isPostPresent(id, login)) {
            throw new AccessDeniedException("User can update or delete only own posts");
        }
    }

    private boolean isPostPresent(Long id, String login) {
        return postDAO.getAllCreatedByCurrentUser(login).stream().anyMatch(post -> id.equals(post.getId()));
    }

    private List<PostDto> getLastFiveActivityFeedSortedByDateWithPages(List<Post> posts, Long currentUserId, int pageSize, int pageNumber) {
        User currentUser = userDAO.getById(currentUserId);

        List<Post> lastPosts = posts.stream()
                .filter(post -> currentUser.getSubscribers().contains(post.getUser()))
                .sorted(Comparator.comparing(Post::getDate).reversed())
                .limit(LAST_FIVE_POSTS)
                .collect(Collectors.toList());

        return postConverter.convertToListPostDto(getPage(lastPosts, pageSize, pageNumber));
    }

    private List<Post> getPage(List<Post> posts, int pageSize, int pageNumber) {
        List<List<Post>> pages = new ArrayList<>();

        for (int i = 0; i < posts.size(); i += pageSize) {
            List<Post> page = new ArrayList<>(posts.subList(i, Math.min(posts.size(), i + pageSize)));

            pages.add(page);
        }
        if (!posts.isEmpty() && pageNumber - 1 <= posts.size() / pageSize) {
            return pages.get(pageNumber - 1);
        } else {
            return Collections.emptyList();
        }
    }
}
