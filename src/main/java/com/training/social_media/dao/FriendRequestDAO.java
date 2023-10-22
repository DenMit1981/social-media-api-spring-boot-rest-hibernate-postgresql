package com.training.social_media.dao;

import com.training.social_media.model.FriendRequest;
import com.training.social_media.model.User;

import java.util.List;

public interface FriendRequestDAO {

    void save(FriendRequest friendRequest);

    List<FriendRequest> getAll();

    FriendRequest getById(Long id);

    FriendRequest getBySenderAndReceiver(User sender, User receiver);

    void deleteById(Long id);
}
