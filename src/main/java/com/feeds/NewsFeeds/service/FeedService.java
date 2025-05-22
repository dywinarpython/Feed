package com.feeds.NewsFeeds.service;

import com.feeds.NewsFeeds.entity.Feed;
import com.feeds.NewsFeeds.repository.FeedRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FeedService {


    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private CacheManager cacheManager;

    @Value("${size.feed}")
    private Integer size;


    public void createFeedForFriend(RequestFriendDTOFeed requestFriendDTOFeed) {
        Cache cache = cacheManager.getCache("LIST_FEED_USER");
        if (cache == null){
            throw new RuntimeException("Кеш не доступен");
        }
        List<Feed> feedList = new ArrayList<>();
        String[] requestFeedDTOList = feedRepository.findPostByNickname(requestFriendDTOFeed.getNickname());
        Long idFriend1 = feedRepository.findIDByNickname(requestFriendDTOFeed.getNickname2());
        for (String namePost: requestFeedDTOList){
            Feed feed = new Feed();
            feed.setUserId(idFriend1);
            feed.setNamePost(namePost);
            feedList.add(feed);
        }
        requestFeedDTOList = feedRepository.findPostByNickname(requestFriendDTOFeed.getNickname2());
        Long idFriend2 = feedRepository.findIDByNickname(requestFriendDTOFeed.getNickname());
        for (String namePost: requestFeedDTOList){
            Feed feed = new Feed();
            feed.setUserId(idFriend2);
            feed.setNamePost(namePost);
            feedList.add(feed);
        }
        feedRepository.saveAll(feedList);
        pushCache(idFriend1, cache);
        pushCache(idFriend2, cache);
    }



    public void createFollowersFeed(RequestFollowersFeedDTO requestFollowersFeedDTO){
        Cache cache = cacheManager.getCache("LIST_FEED_USER");
        List<Feed> feedList = new ArrayList<>();
        String[] requestFeedDTOList = feedRepository.findPostByNicknameForCommunity(requestFollowersFeedDTO.getNicknameCommunity());
        Long userId = feedRepository.findIDByNickname(requestFollowersFeedDTO.getNickname());
        for (String namePost: requestFeedDTOList){
            Feed feed = new Feed();
            feed.setUserId(userId);
            feed.setNamePost(namePost);
            feedList.add(feed);
        }
        feedRepository.saveAll(feedList);
        pushCache(userId, cache);
    }

    public void addNewFeedFriends(ConsumerRecord<String, String> record){
        Long[] idFriends = feedRepository.getFriendsForCreate(record.key());
        Cache cache = cacheManager.getCache("LIST_FEED_USER");
        if (cache == null){
            throw new RuntimeException("Кеш не доступен");
        }
        createFeed(idFriends, record.value(), cache);
    }

    public void addNewFeedFollowers(ConsumerRecord<String, String> record){
        Long[] idFollowers = feedRepository.getFollowers(record.key());
        Cache cache = cacheManager.getCache("LIST_FEED_USER");
        if (cache == null){
            throw new RuntimeException("Кеш не доступен");
        }
        createFeed(idFollowers, record.value(), cache);
    }
    @Transactional
    public void deleteFeedForFollower(DeleteFollowerDTO deleteFollowerDTO) {
        feedRepository.delFollowerForCommunity(deleteFollowerDTO.getId(), deleteFollowerDTO.getIdCommunity());
    }


    @Transactional
    public void deleteFeedForFriend(DeleteFriendDTO deleteFriendDTO) {
        feedRepository.delFriendFeedAll(deleteFriendDTO.getId1(), deleteFriendDTO.getId2());
    }

    @Transactional
    public void deleteFeedForAllFriendsOrFollowersByNamePost(String namePost)  {
        feedRepository.delByNamePost(namePost);;
    }



    @Transactional
    public void delNewFeedFollowersAll(){
        feedRepository.clearFeed();
    }
    @Transactional
    public void delNewFeedFriendsAll()  {
        feedRepository.clearFeed();
    }



    private void pushCache(Long id, Cache cache) {
        List<FeedDTO> feedDTOList = feedRepository.findByUserIdOrderByCreateTimeDesc(id);
        int feedListSize = feedDTOList.size();
        int totalPages = (feedListSize + size - 1) / size;
        if(cache == null){
            throw new RuntimeException("Кеш не найден");
        }
        for (int page = 0; page < totalPages; page++) {
            int from = page * size;
            int to = Math.min(from + size,feedListSize);
            List<FeedDTO> subList = feedDTOList.subList(from, to);
            cache.put(id + ":" + page, new ListFeedDTO(subList));
        }
    }

    private void createFeed(Long[] ids, String namePost, Cache cache){
        for(Long id: ids){
            Feed feed = new Feed();
            feed.setNamePost(namePost);
            feed.setUserId(id);
            feedRepository.save(feed);
            pushCache(id, cache);
        }
    }



}


