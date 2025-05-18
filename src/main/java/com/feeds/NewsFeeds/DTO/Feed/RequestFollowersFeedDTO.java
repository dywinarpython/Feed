package com.feeds.NewsFeeds.DTO.Feed;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RequestFollowersFeedDTO {

    @NotNull
    private String nickname;

    @NotNull
    private String nicknameCommunity;
}
