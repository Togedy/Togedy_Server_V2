package com.togedy.togedy_server_v2.domain.chat.dto.client;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostQuestionResponse {

    private String answer;

    private Boolean isFollowUpRequired;

    public PostQuestionResponse of(String answer, boolean isFollowUpRequired) {
        return PostQuestionResponse.builder()
                .answer(answer)
                .isFollowUpRequired(isFollowUpRequired)
                .build();
    }
}
