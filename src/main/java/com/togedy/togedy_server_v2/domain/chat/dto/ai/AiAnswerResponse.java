package com.togedy.togedy_server_v2.domain.chat.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiAnswerResponse {

    private String answer;

    private boolean reply;

    private String location;

    @JsonProperty("NER_Page_1")
    private String nerPage1;

    @JsonProperty("NER_Page_2")
    private String nerPage2;

    @JsonProperty("NER_Page_3")
    private String nerPage3;

}
