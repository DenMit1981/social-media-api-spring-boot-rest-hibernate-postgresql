package com.training.social_media.controller;

import com.training.social_media.model.FriendRequest;
import com.training.social_media.service.FriendRequestService;
import com.training.social_media.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.security.Principal;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/friend-requests")
@Api("Friend request controller")
public class FriendRequestController {

    private final FriendRequestService friendRequestService;
    private final UserService userService;

    @PostMapping("/send/{receiverId}")
    @ApiOperation(value = "Send friend request", authorizations = @Authorization(value = "Bearer"))
    public ResponseEntity<?> sendFriendRequest(Principal principal, @PathVariable Long receiverId) {
        FriendRequest sentRequest = friendRequestService.sendFriendRequest(principal.getName(), receiverId);

        return getPostResponseForFriendRequest(sentRequest);
    }

    @PostMapping("/accept/{requestId}")
    @ApiOperation(value = "Accept friend request", authorizations = @Authorization(value = "Bearer"))
    public ResponseEntity<?> acceptFriendRequest(Principal principal, @PathVariable Long requestId) {
        FriendRequest acceptedRequest = friendRequestService.acceptFriendRequest(principal.getName(), requestId);

        return getPostResponseForFriendRequest(acceptedRequest);
    }

    @PostMapping("/reject/{requestId}")
    @ApiOperation(value = "Reject friend request", authorizations = @Authorization(value = "Bearer"))
    public ResponseEntity<?> rejectFriendRequest(Principal principal, @PathVariable Long requestId) {
        FriendRequest rejectedRequest = friendRequestService.rejectFriendRequest(principal.getName(), requestId);

        return getPostResponseForFriendRequest(rejectedRequest);
    }

    @PostMapping("/cancel/{receiverId}")
    @ApiOperation(value = "Cancel friend request", authorizations = @Authorization(value = "Bearer"))
    public ResponseEntity<?> cancelFriendRequest(Principal principal, @PathVariable Long receiverId) {
        FriendRequest canceledRequest = friendRequestService.cancelFriendRequest(principal.getName(), receiverId);

        return getPostResponseForFriendRequest(canceledRequest);
    }

    @DeleteMapping("/{friendId}")
    @ApiOperation(value = "Delete friend", authorizations = @Authorization(value = "Bearer"))
    public ResponseEntity<String> deleteFriend(Principal principal, @PathVariable Long friendId) {
        friendRequestService.deleteFriend(principal.getName(), friendId);

        return ResponseEntity.ok("Friend removed successfully");
    }

    private ResponseEntity<?> getPostResponseForFriendRequest(FriendRequest request) {
        String currentUri = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        String requestLocation = currentUri + "/" + request.getId();

        return ResponseEntity.status(CREATED)
                .header(HttpHeaders.LOCATION, requestLocation)
                .body(request);
    }
}
