package com.feeds.NewsFeeds.controllers;


import com.feeds.NewsFeeds.DTO.Feed.CreateFeedDTO;
import com.feeds.NewsFeeds.Service.FeedService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.ValidationException;

@RestController
@RequestMapping("v1/api/feed")
public class NewsFeedsController {

    @Autowired
    private FeedService feedService;

    @PostMapping
    public ResponseEntity<String> createFeed(@Valid @RequestBody CreateFeedDTO createFeedDTO, BindingResult result) throws ValidationException {
        feedService.createFeedForAllFriendsOrFolowers(createFeedDTO, result);
        return ResponseEntity.ok("Лента для пользователь обновлена");
    }
}
