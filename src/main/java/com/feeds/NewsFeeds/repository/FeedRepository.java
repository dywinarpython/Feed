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
                 delete from feed
                 where id in(select f.id
                 from feed f
                 where f.name_post not in (select pua."name" from posts_user_app pua ) and f.name_post  not in (select pc."name" from posts_community pc))
           """, nativeQuery = true
    )
    void clearFeed();

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
                    join posts_community pc on pc.community_id = :idCommunity
                    where f.user_id = :id and pc."name" = f.name_post)""", nativeQuery = true)
    void delFollowerForCommunity(@Param("id") Long id, @Param("idCommunity") Long idCommunity);
    





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
