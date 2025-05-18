package com.feeds.NewsFeeds.Service;

import com.feeds.NewsFeeds.DTO.Feed.*;
import com.feeds.NewsFeeds.entity.Feed;
import com.feeds.NewsFeeds.repository.FeedRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import javax.xml.bind.ValidationException;
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


    @Transactional
    public void createFeedForFriend(RequestFriendDTOFeed requestFriendDTOFeed, BindingResult bindingResult) throws ValidationException {
        if (bindingResult.hasErrors()) {
            log.error(bindingResult.getAllErrors().toString());
            throw new ValidationException("Ошибка валидации данных");
        }
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


    @Transactional
    public void createFollowersFeed(RequestFollowersFeedDTO requestFollowersFeedDTO, BindingResult bindingResult) throws ValidationException {
        if (bindingResult.hasErrors()) {
            log.error(bindingResult.getAllErrors().toString());
            throw new ValidationException("Ошибка валидации данных");
        }
        Cache cache = cacheManager.getCache("LIST_FEED_FOLLOWERS");
        List<Feed> feedList = new ArrayList<>();
        String[] requestFeedDTOList = feedRepository.findPostByNicknameForCommunity(requestFollowersFeedDTO.getNickname());
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


    @Transactional
    public void createFeedForAllFriendsOrFollowers(RequestFeedDTO createFeedDTO, BindingResult bindingResult) throws ValidationException {
        if (bindingResult.hasErrors()) {
            log.error(bindingResult.getAllErrors().toString());
            throw new ValidationException("Ошибка валидации данных");
        }
        switch (createFeedDTO.getIdAuthor()){
            case 0 -> addNewFeedFriends(createFeedDTO);
            case 1 -> addNewFeedFollowers(createFeedDTO);
            default -> throw new ValidationException("Некоректны данные, а именно IdAuthor");
        }
    }


    @Transactional
    public void deleteFeedForFollower(RequestFollowersFeedDTO requestFollowersFeedDTO, BindingResult bindingResult) throws ValidationException {
        if (bindingResult.hasErrors()) {
            log.error(bindingResult.getAllErrors().toString());
            throw new ValidationException("Ошибка валидации данных");
        }
        feedRepository.delFollowerForCommunity(requestFollowersFeedDTO.getNicknameCommunity(), requestFollowersFeedDTO.getNickname());
    }

    @Transactional
    public void deleteFeedForFriend(RequestFriendDTOFeed requestFriendDTOFeed, BindingResult bindingResult) throws ValidationException {
        if (bindingResult.hasErrors()) {
            log.error(bindingResult.getAllErrors().toString());
            throw new ValidationException("Ошибка валидации данных");
        }
        feedRepository.delFriendFeedAll(requestFriendDTOFeed.getNickname(), requestFriendDTOFeed.getNickname2());
    }

    @Transactional
    public void deleteFeedForAllFriendsOrFollowersByNamePost(RequestFeedDTO requestFeedDTO, BindingResult bindingResult) throws ValidationException {
        if (bindingResult.hasErrors()) {
            log.error(bindingResult.getAllErrors().toString());
            throw new ValidationException("Ошибка валидации данных");
        }
        delByNamePost(requestFeedDTO.getNamePost());
    }


    @Transactional
    public void deleteFeedForAllFriendsOrFollowersAll(RequestFeedDTO requestFeedDTO, BindingResult bindingResult) throws ValidationException {
        if (bindingResult.hasErrors()) {
            log.error(bindingResult.getAllErrors().toString());
            throw new ValidationException("Ошибка валидации данных");
        }
        switch (requestFeedDTO.getIdAuthor()){
            case 0 -> delNewFeedFriendsAll(requestFeedDTO.getNickname());
            case 1 -> delNewFeedFollowersAll(requestFeedDTO.getNickname());
            default -> throw new ValidationException("Некоректны данные, а именно IdAuthor");
        }
    }

    private void delNewFeedFollowersAll(String nickname){
        feedRepository.delFollowersFeed(nickname);
    }

    private void delNewFeedFriendsAll(String nickname)  {
        feedRepository.delFriendFeed(nickname);
    }

    private void delByNamePost(String namePost){
        feedRepository.delByNamePost(namePost);
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

    private void createFeed(Long[] ids, RequestFeedDTO requestFeedDTO, Cache cache){
        for(Long id: ids){
            Feed feed = new Feed();
            feed.setNamePost(requestFeedDTO.getNamePost());
            feed.setUserId(id);
            feedRepository.save(feed);
            pushCache(id, cache);
        }
    }

    private void addNewFeedFriends(RequestFeedDTO requestFeedDTO){
        Long[] idFriends = feedRepository.getFriendsForCreate(requestFeedDTO.getNickname());
        Cache cache = cacheManager.getCache("LIST_FEED_USER");
        if (cache == null){
            throw new RuntimeException("Кеш не доступен");
        }
        createFeed(idFriends, requestFeedDTO, cache);
    }

    private void addNewFeedFollowers(RequestFeedDTO requestFeedDTO){
        Long[] idFollowers = feedRepository.getFollowers(requestFeedDTO.getNickname());
        Cache cache = cacheManager.getCache("LIST_FEED_FOLLOWERS");
        if (cache == null){
            throw new RuntimeException("Кеш не доступен");
        }
        createFeed(idFollowers, requestFeedDTO, cache);
    }

}
