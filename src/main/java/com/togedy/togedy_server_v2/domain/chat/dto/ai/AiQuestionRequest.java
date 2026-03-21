package com.togedy.togedy_server_v2.domain.chat.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.togedy.togedy_server_v2.domain.chat.entity.NerKeyword;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AiQuestionRequest {

    @JsonProperty("question_1")
    private String question1;

    @JsonProperty("question_2")
    private String question2;

    @JsonProperty("first")
    private Boolean isFirst;

    @JsonProperty("NER_Keyword")
    private NerKeyword nerKeyword;

    public static AiQuestionRequest of(String question1) {
        return AiQuestionRequest.builder()
                .question1(question1)
                .question2("")
                .isFirst(true)
                .build();
    }

    public static AiQuestionRequest of(String question1, String question2, NerKeyword nerKeyword) {
        return AiQuestionRequest.builder()
                .question1(question1)
                .question2(question2)
                .isFirst(false)
                .nerKeyword(nerKeyword)
                .build();
    }
}
