package com.training.social_media.dao.impl;

import com.training.social_media.dao.FriendRequestDAO;
import com.training.social_media.exception.RequestNotFoundException;
import com.training.social_media.model.FriendRequest;
import com.training.social_media.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@AllArgsConstructor
public class FriendRequestDAOImpl implements FriendRequestDAO {

    private static final String QUERY_SELECT_FROM_FRIEND_REQUEST = "from FriendRequest";
    private static final String QUERY_DELETE_FROM_FRIEND_REQUEST_BY_REQUEST_ID = "delete from FriendRequest fr where fr.id =:id";
    private static final String FRIEND_REQUEST_NOT_FOUND = "Request with id %s not found";

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public void save(FriendRequest friendRequest) {
        entityManager.persist(friendRequest);
    }

    @Override
    public List<FriendRequest> getAll() {
        return entityManager.createQuery(QUERY_SELECT_FROM_FRIEND_REQUEST, FriendRequest.class)
                .getResultList();
    }

    @Override
    public FriendRequest getById(Long id) {
        return getAll().stream()
                .filter(request -> id.equals(request.getId()))
                .findAny()
                .orElseThrow(() -> new RequestNotFoundException(String.format(FRIEND_REQUEST_NOT_FOUND, id)));
    }

    @Override
    public FriendRequest getBySenderAndReceiver(User sender, User receiver) {
        return getAll().stream()
                .filter(request -> request.getSender().equals(sender) && request.getReceiver().equals(receiver))
                .findAny()
                .orElseThrow(() -> new RequestNotFoundException(FRIEND_REQUEST_NOT_FOUND));
    }

    @Override
    public void deleteById(Long id) {
        entityManager.createQuery(QUERY_DELETE_FROM_FRIEND_REQUEST_BY_REQUEST_ID)
                .setParameter("id", id)
                .executeUpdate();
    }
}
