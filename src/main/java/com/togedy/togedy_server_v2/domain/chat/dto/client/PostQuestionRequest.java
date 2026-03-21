package com.togedy.togedy_server_v2.domain.chat.dto.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostQuestionRequest {

    private String question;

    private String followUpAnswer;

}
