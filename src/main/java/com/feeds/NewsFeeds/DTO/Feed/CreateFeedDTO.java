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
    private String author;
}
