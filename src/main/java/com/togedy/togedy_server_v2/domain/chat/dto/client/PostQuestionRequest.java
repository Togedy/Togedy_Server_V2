package com.togedy.togedy_server_v2.domain.chat.dto.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostQuestionRequest {

    private String question;

    private String followUpAnswer;

}
