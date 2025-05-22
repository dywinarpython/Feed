package com.feeds.NewsFeeds.handler;


import com.feeds.NewsFeeds.service.FeedService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.*;
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

    @KafkaListener(topics = "news-feed-topic-community")
    public void createFeedFollowers(ConsumerRecord<String, String> record) {
        feedService.addNewFeedFollowers(record);
    }

    @KafkaListener(topics = "news-feed-topic-friend")
    public void createFeedFriend(RequestFriendDTOFeed requestFriendDTOFeed){
        feedService.createFeedForFriend(requestFriendDTOFeed);
    }

    @KafkaListener(topics = "news-feed-topic-friend-del")
    public void deleteFeedFriend(DeleteFriendDTO deleteFriendDTO) {
        feedService.deleteFeedForFriend(deleteFriendDTO);
    }

    @KafkaListener(topics = "news-feed-topic-follower")
    public void createFollowerFeed(RequestFollowersFeedDTO requestFollowersFeedDTO) {
        feedService.createFollowersFeed(requestFollowersFeedDTO);
    }
    @KafkaListener(topics = "news-feed-topic-follower-del")
    public void deleteFeedFollower(DeleteFollowerDTO deleteFollowerDTO) {
        feedService.deleteFeedForFollower(deleteFollowerDTO);
    }

    @KafkaListener(topics = "news-feed-topic-namePost-del")
    public void deleteByNamePost(String namePost) {
        feedService.deleteFeedForAllFriendsOrFollowersByNamePost(namePost);
    }

    @KafkaListener(topics = "news-feed-topic-user-del")
    public void deleteUser(String nickname){
        log.info("Удаление избыточных данных в ленте пользователей, для связанных чем либо пользователем: {}", nickname);
        feedService.delNewFeedFriendsAll();
    }

    @KafkaListener(topics = "news-feed-topic-community-del")
    public void deleteCommunity(String nickname)  {
        log.info("Удаление избыточных данных в ленте пользователей, для связанных чем либо сообществом: {}", nickname);
        feedService.delNewFeedFollowersAll();
    }
}
