package com.training.social_media.dao.impl;

import com.training.social_media.dao.ImageDAO;
import com.training.social_media.exception.ImageNotFoundException;
import com.training.social_media.model.Image;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@AllArgsConstructor
public class ImageDAOImpl implements ImageDAO {

    private static final String POST_ID = "postId";
    private static final String IMAGE_NAME = "imageName";
    private static final String IMAGE_NOT_FOUND = "Image with id %s not found";
    private static final String QUERY_SELECT_FROM_IMAGE_BY_POST_ID = "from Image i where i.post.id =:postId";
    private static final String QUERY_DELETE_FROM_IMAGE_BY_POST_ID_AND_IMAGE_NAME = "delete from Image i where i.post.id =:postId and i.name =:imageName";

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public void save(Image image) {
        entityManager.persist(image);
    }

    @Override
    public List<Image> getAllByPostId(Long postId) {
        return entityManager.createQuery(QUERY_SELECT_FROM_IMAGE_BY_POST_ID, Image.class)
                .setParameter(POST_ID, postId)
                .getResultList();
    }

    @Override
    public Image getByImageIdAndPostId(Long imageId, Long postId) {
        return getAllByPostId(postId)
                .stream()
                .filter(image -> imageId.equals(image.getId()))
                .findAny()
                .orElseThrow(() -> new ImageNotFoundException(String.format(IMAGE_NOT_FOUND, imageId)));
    }

    @Override
    public void deleteByImageNameAndPostId(String imageName, Long postId) {
        entityManager.createQuery(QUERY_DELETE_FROM_IMAGE_BY_POST_ID_AND_IMAGE_NAME)
                .setParameter(IMAGE_NAME, imageName)
                .setParameter(POST_ID, postId)
                .executeUpdate();
    }
}
