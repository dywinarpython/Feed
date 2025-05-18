package com.feeds.NewsFeeds.controllers;


import com.feeds.NewsFeeds.DTO.Feed.RequestFollowersFeedDTO;
import com.feeds.NewsFeeds.DTO.Feed.RequestFriendDTOFeed;
import com.feeds.NewsFeeds.DTO.Feed.RequestFeedDTO;
import com.feeds.NewsFeeds.Service.FeedService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;

@RestController
@RequestMapping("v1/api/feed")
public class NewsFeedsController {

    @Autowired
    private FeedService feedService;

    @PostMapping
    public ResponseEntity<String> createFeed(@Valid @RequestBody RequestFeedDTO createFeedDTO, BindingResult result) throws ValidationException {
        feedService.createFeedForAllFriendsOrFollowers(createFeedDTO, result);
        return ResponseEntity.ok("Лента для пользователь обновлена");
    }
    @PostMapping("/friend")
    public ResponseEntity<String> createFeedFriend(@Valid @RequestBody RequestFriendDTOFeed requestFriendDTOFeed, BindingResult result) throws ValidationException {
        feedService.createFeedForFriend(requestFriendDTOFeed, result);
        return ResponseEntity.ok("Лента для пользователь обновлена");
    }
    @DeleteMapping("/friend")
    public ResponseEntity<String> deleteFeedFriend(@Valid @RequestBody RequestFriendDTOFeed requestFriendDTOFeed, BindingResult result) throws ValidationException {
        feedService.deleteFeedForFriend(requestFriendDTOFeed, result);
        return ResponseEntity.ok("Лента для пользователь обновлена");
    }
    @PostMapping("/follower")
    public ResponseEntity<String> createFollowerFeed(@Valid @RequestBody RequestFollowersFeedDTO requestFollowersFeedDTO, BindingResult result) throws ValidationException {
        feedService.createFollowersFeed(requestFollowersFeedDTO, result);
        return ResponseEntity.ok("Лента для пользователь обновлена");
    }
    @DeleteMapping("/follower")
    public ResponseEntity<String> deleteFeedFollower(@Valid @RequestBody RequestFollowersFeedDTO requestFollowersFeedDTO, BindingResult result) throws ValidationException {
        feedService.deleteFeedForFollower(requestFollowersFeedDTO, result);
        return ResponseEntity.ok("Лента для пользователь обновлена");
    }
    @DeleteMapping("/namePost")
    public ResponseEntity<String> deleteByNamePost(@Valid @RequestBody RequestFeedDTO createFeedDTO, BindingResult result) throws ValidationException {
        feedService.deleteFeedForAllFriendsOrFollowersByNamePost(createFeedDTO, result);
        return ResponseEntity.ok("Лента для пользователь обновлена");
    }
    @DeleteMapping
    public ResponseEntity<String> deleteAll(@Valid @RequestBody RequestFeedDTO requestFeedDTO, BindingResult result) throws ValidationException {
        feedService.deleteFeedForAllFriendsOrFollowersAll(requestFeedDTO, result);
        return ResponseEntity.ok("Лента для пользователь обновлена");
    }
}
