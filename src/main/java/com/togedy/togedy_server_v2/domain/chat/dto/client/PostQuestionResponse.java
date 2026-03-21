package com.togedy.togedy_server_v2.domain.chat.dto.client;

import com.togedy.togedy_server_v2.domain.chat.dto.ai.AiAnswerResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostQuestionResponse {

    private String answer;

    private Boolean isFollowUpRequired;

    public static PostQuestionResponse of(AiAnswerResponse aiAnswerResponse) {
        return PostQuestionResponse.builder()
                .answer(aiAnswerResponse.getAnswer())
                .isFollowUpRequired(aiAnswerResponse.isReply())
                .build();
    }
}
