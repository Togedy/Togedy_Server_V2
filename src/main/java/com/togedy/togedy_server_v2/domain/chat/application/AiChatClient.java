package com.togedy.togedy_server_v2.domain.chat.application;

import com.togedy.togedy_server_v2.domain.chat.dto.ai.AiAnswerResponse;
import com.togedy.togedy_server_v2.domain.chat.dto.ai.AiQuestionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AiChatClient {

    private final WebClient aiWebClient;

    public Mono<AiAnswerResponse> requestQuestion(AiQuestionRequest request) {
        return aiWebClient.post()
                .uri("/answer")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AiAnswerResponse.class);
    }
}
