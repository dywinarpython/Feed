package com.feeds.NewsFeeds.Service;

import com.feeds.NewsFeeds.DTO.Feed.CreateFeedDTO;
import com.feeds.NewsFeeds.DTO.Feed.FeedDTO;
import com.feeds.NewsFeeds.DTO.Feed.ListFeedDTO;
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
    public void createFeedForAllFriendsOrFollowers(CreateFeedDTO createFeedDTO, BindingResult bindingResult) throws ValidationException {
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

    public void pushCache(Long id, Cache cache) {
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

    public void addNewFeedFriends(CreateFeedDTO createFeedDTO){
        Long[] idFriends = feedRepository.getFriends(createFeedDTO.getNickname());
        Cache cache = cacheManager.getCache("LIST_FEED_USER");
        if (cache == null){
            throw new RuntimeException("Кеш не доступен");
        }
        for(Long id: idFriends){
            Feed feed = new Feed();
            feed.setNamePost(createFeedDTO.getNamePost());
            feed.setUserId(id);
            feedRepository.save(feed);
            pushCache(id, cache);
        }
    }

    public void addNewFeedFollowers(CreateFeedDTO createFeedDTO){
        Long[] idFriends = feedRepository.getFollowers(createFeedDTO.getNickname());
        Cache cache = cacheManager.getCache("LIST_FEED_FOLLOWERS");
        if (cache == null){
            throw new RuntimeException("Кеш не доступен");
        }
        for(Long id: idFriends){
            Feed feed = new Feed();
            feed.setNamePost(createFeedDTO.getNamePost());
            feed.setUserId(id);
            feedRepository.save(feed);
            pushCache(id, cache);
        }
    }
}
