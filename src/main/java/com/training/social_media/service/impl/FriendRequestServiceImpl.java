package com.training.social_media.service.impl;

import com.training.social_media.dao.FriendRequestDAO;
import com.training.social_media.dao.UserDAO;
import com.training.social_media.exception.AccessDeniedException;
import com.training.social_media.exception.InvalidRequestException;
import com.training.social_media.model.FriendRequest;
import com.training.social_media.model.User;
import com.training.social_media.model.enums.Status;
import com.training.social_media.service.FriendRequestService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class FriendRequestServiceImpl implements FriendRequestService {

    private final UserDAO userDAO;
    private final FriendRequestDAO friendRequestDAO;

    @Override
    @Transactional
    public FriendRequest sendFriendRequest(String senderLogin, Long receiverId) {
        User sender = userDAO.getByLogin(senderLogin);
        User receiver = userDAO.getById(receiverId);

        FriendRequest request = new FriendRequest();

        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus(Status.PENDING);
        request.setDate(LocalDateTime.now());

        sender.getSubscribers().add(receiver);

        userDAO.update(sender);

        friendRequestDAO.save(request);

        return request;
    }

    @Override
    @Transactional
    public FriendRequest acceptFriendRequest(String receiverName, Long requestId) {
        FriendRequest request = friendRequestDAO.getById(requestId);

        checkRequestStatus(request);

        User receiver = userDAO.getByLogin(receiverName);
        User sender = request.getSender();

        receiver.getFriends().add(sender);
        sender.getFriends().add(receiver);

        userDAO.update(receiver);
        userDAO.update(sender);

        request.setStatus(Status.ACCEPTED);

        friendRequestDAO.save(request);

        return request;
    }

    @Override
    @Transactional
    public FriendRequest rejectFriendRequest(String receiverName, Long requestId) {
        FriendRequest request = friendRequestDAO.getById(requestId);

        checkRequestStatus(request);

        User receiver = userDAO.getByLogin(receiverName);

        if (!request.getReceiver().equals(receiver)) {
            throw new AccessDeniedException("Unauthorized to reject this request");
        }

        request.setStatus(Status.REJECTED);

        friendRequestDAO.save(request);

        return request;
    }

    @Override
    @Transactional
    public FriendRequest cancelFriendRequest(String senderLogin, Long receiverId) {
        User sender = userDAO.getByLogin(senderLogin);
        User receiver = userDAO.getById(receiverId);

        FriendRequest request = friendRequestDAO.getBySenderAndReceiver(sender, receiver);

        checkRequestStatus(request);

        friendRequestDAO.deleteById(request.getId());

        sender.getSubscribers().remove(receiver);

        userDAO.update(sender);

        return request;
    }

    @Override
    @Transactional
    public void deleteFriend(String currentUserName, Long friendToRemoveId) {
        User currentUser = userDAO.getByLogin(currentUserName);
        User friendToRemove = userDAO.getById(friendToRemoveId);

        currentUser.getFriends().remove(friendToRemove);
        currentUser.getSubscribers().remove(friendToRemove);

        friendToRemove.getFriends().remove(currentUser);

        userDAO.update(currentUser);
        userDAO.update(friendToRemove);
    }

    private void checkRequestStatus(FriendRequest request) {
        if (request.getStatus() != Status.PENDING) {
            throw new InvalidRequestException("Invalid request");
        }
    }
}
