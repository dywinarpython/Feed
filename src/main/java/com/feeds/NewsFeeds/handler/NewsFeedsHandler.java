package com.feeds.NewsFeeds.handler;


import
import com.feeds.NewsFeeds.Service.FeedService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NewsFeedsHandler {

    @Autowired
    private FeedService feedService;

    @KafkaListener(topics = "news-feed-topic-user")
    public void createFeedFriends(ConsumerRecord<String, String> record) {
        feedService.addNewFeedFriends(record);
    }

    @KafkaListener(topics = "news-feed-topic-friend")
    public void createFeedFriend(RequestFriendDTOFeed requestFriendDTOFeed){
        feedService.createFeedForFriend(requestFriendDTOFeed);
    }

    @KafkaListener(topics = "news-feed-topic-friend-del")
    public void deleteFeedFriend(RequestFriendDTOFeed requestFriendDTOFeed) {
        feedService.deleteFeedForFriend(requestFriendDTOFeed);
    }

//    @PostMapping("/follower")
//    public ResponseEntity<String> createFollowerFeed(@Valid @RequestBody RequestFollowersFeedDTO requestFollowersFeedDTO, BindingResult result) throws ValidationException {
//        feedService.createFollowersFeed(requestFollowersFeedDTO, result);
//        return ResponseEntity.ok("Лента для пользователь обновлена");
//    }
//    @DeleteMapping("/follower")
//    public ResponseEntity<String> deleteFeedFollower(@Valid @RequestBody RequestFollowersFeedDTO requestFollowersFeedDTO, BindingResult result) throws ValidationException {
//        feedService.deleteFeedForFollower(requestFollowersFeedDTO, result);
//        return ResponseEntity.ok("Лента для пользователь обновлена");
//    }
//    @DeleteMapping("/namePost")
//    public ResponseEntity<String> deleteByNamePost(@Valid @RequestBody RequestFeedDTO createFeedDTO, BindingResult result) throws ValidationException {
//        feedService.deleteFeedForAllFriendsOrFollowersByNamePost(createFeedDTO, result);
//        return ResponseEntity.ok("Лента для пользователь обновлена");
//    }
//    @DeleteMapping
//    public ResponseEntity<String> deleteAll(@Valid @RequestBody RequestFeedDTO requestFeedDTO, BindingResult result) throws ValidationException {
//        feedService.deleteFeedForAllFriendsOrFollowersAll(requestFeedDTO, result);
//        return ResponseEntity.ok("Лента для пользователь обновлена");
//    }
}
