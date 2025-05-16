package com.feeds.NewsFeeds.repository;

import com.feeds.NewsFeeds.DTO.Feed.FeedDTO;
import com.feeds.NewsFeeds.entity.Feed;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface FeedRepository extends JpaRepository<Feed, Long> {
    @Query(
            value = """
                 SELECT friend_id
                          FROM friends f
                          JOIN users_app u ON f.user_id = u.id
                          where u.nickname = :nickname
           """, nativeQuery = true
    )
    Long[] getFriends(@Param("nickname") String  nickname);

    @Query(
            value = """
                 SELECT followers_id
                          FROM followers f
                          JOIN community c ON f.community_id = c.id
                          where c.nickname = :nickname
           """, nativeQuery = true
    )
    Long[] getFollowers(@Param("nickname") String  nickname);


    @Query("SELECT  new com.feeds.NewsFeeds.DTO.Feed.FeedDTO(f.userId, f.namePost) FROM Feed f WHERE f.userId = :id ORDER BY f.createTime DESC")
    List<FeedDTO> findByUserIdOrderByCreateTimeDesc(@Param("id") Long id);
}
