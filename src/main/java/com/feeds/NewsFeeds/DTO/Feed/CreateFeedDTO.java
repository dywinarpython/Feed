package com.feeds.NewsFeeds.DTO.Feed;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateFeedDTO {

    @NotNull
    private String nickname;
    @NotNull
    private String namePost;
    @NotNull
    // 0 - User
    // 1 - Community
    private Integer idAuthor;
}
