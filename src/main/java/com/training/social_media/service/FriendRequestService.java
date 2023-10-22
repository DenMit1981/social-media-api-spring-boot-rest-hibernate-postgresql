package com.training.social_media.service;

import com.training.social_media.model.FriendRequest;

public interface FriendRequestService {

    FriendRequest sendFriendRequest(String senderLogin, Long receiverId);

    FriendRequest acceptFriendRequest(String receiverName, Long requestId);

    FriendRequest rejectFriendRequest(String receiverName, Long requestId);

    FriendRequest cancelFriendRequest(String senderLogin, Long receiverId);

    void deleteFriend(String currentUserName, Long friendToRemoveId);
}

