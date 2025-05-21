package com.feeds.NewsFeeds.repository;



import com.feeds.NewsFeeds.entity.Feed;
import org.example.FeedDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface FeedRepository extends JpaRepository<Feed, Long> {
    @Query(
            value = """
                 select f.friend_id
                          FROM friends f
                          JOIN users_app u ON f.user_id = u.id
                          where u.nickname = :nickname
           """, nativeQuery = true
    )
    Long[] getFriendsForCreate(@Param("nickname") String  nickname);



    @Modifying
    @Query(
            value = """
                 delete from feed
                 where id in(
                          select f2.id
                          from friends f
                          join users_app u ON f.user_id = u.id
                          join feed f2 on f2.user_id = f.friend_id
                          where u.nickname = :nickname)
           """, nativeQuery = true
    )
    void delFriendFeed(@Param("nickname") String  nickname);

    @Modifying
    @Query(value = """
            delete from feed
            where id in (
                select f.id
                from feed f
                join users_app ua on ua.id = f.user_id
                where f.name_post in (
                    select pua."name"
                    from posts_user_app pua
                    where pua.users_app_id in (:id1, :id2)
                )
                and f.user_id in (:id1, :id2)
            )""", nativeQuery = true)
    void delFriendFeedAll(
            @Param("id1") Long id1,
            @Param("id2") Long id2
    );

    @Modifying
    @Query(value = """
            delete from feed
            where id IN (select f.id
                                     from feed f
                                     join followers f2 on f2.followers_id = f.user_id
                                     join posts_community pc on pc.community_id = f2.community_id
                                     where f2.community_id = (select id from community where nickname = :nicknameCommunity) and f2.followers_id = (select id from users_app where nickname = :nickname) and f.name_post = pc.name)
            """, nativeQuery = true)
    void delFollowerForCommunity(@Param("nickname") String nickname, @Param("nicknameCommunity") String nicknameCommunity);
    

    @Query(
            value = """
                 SELECT followers_id
                          FROM followers f
                          JOIN community c ON f.community_id = c.id
                          where c.nickname = :nickname
           """, nativeQuery = true
    )
    Long[] getFollowers(@Param("nickname") String  nickname);

    @Modifying
    @Query(
            value = """
                 DELETE FROM feed
                 WHERE id in (
                 SELECT f.id
                          FROM feed f
                          join users_app ua on f.user_id = ua.id
                          JOIN community c ON c.user_owner_id= ua.id
                          where c.nickname = :nickname)
           """, nativeQuery = true
    )
    void delFollowersFeed(@Param("nickname") String  nickname);

    @Modifying
    @Query(
            value = """
                 DELETE FROM feed
                 WHERE id in (
                 SELECT f.id
                 FROM Feed f
                 where f.name_post = :namePost)
           """, nativeQuery = true
    )
    void  delByNamePost(@Param("namePost") String  namePost);



    @Query("SELECT  new org.example.FeedDTO(f.userId, f.namePost) FROM Feed f WHERE f.userId = :id ORDER BY f.createTime DESC")
    List<FeedDTO> findByUserIdOrderByCreateTimeDesc(@Param("id") Long id);


    @Query(
            value = """
                    SELECT pua.name
                    FROM  users_app ua
                    JOIN posts_user_app pua on pua.users_app_id = ua.id
                    WHERE nickname = :nickname
            """,
            nativeQuery = true
    )
    String[] findPostByNickname(@Param("nickname") String nickname);

    @Query(
            value = """
                    select id
                    from users_app ua
                    where  nickname = :nickname
            """,
            nativeQuery = true
    )
    Long findIDByNickname(@Param("nickname") String nickname);


    @Query(value = """
            select pc.name
            from posts_community pc
            join community c on pc.community_id = c.id
            where  nickname = :nickname
            """, nativeQuery = true)
    String[] findPostByNicknameForCommunity(@Param("nickname") String nickname);
    
    
    


}
