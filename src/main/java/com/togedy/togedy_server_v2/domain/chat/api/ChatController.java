package com.togedy.togedy_server_v2.domain.chat.api;

import com.togedy.togedy_server_v2.domain.chat.application.ChatService;
import com.togedy.togedy_server_v2.domain.chat.dto.client.PostQuestionRequest;
import com.togedy.togedy_server_v2.domain.chat.dto.client.PostQuestionResponse;
import com.togedy.togedy_server_v2.global.response.ApiResponse;
import com.togedy.togedy_server_v2.global.security.AuthUser;
import com.togedy.togedy_server_v2.global.util.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/chatbots")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "챗봇 질문", description = "챗봇에게 질문하고 답변을 받는다.")
    @PostMapping("/questions")
    public ApiResponse<PostQuestionResponse> processQuestion(
            @RequestBody PostQuestionRequest request,
            @AuthenticationPrincipal AuthUser user
    ) {
        PostQuestionResponse response = chatService.handleQuestion(request, user.getId());
        return ApiUtil.success(response);
    }
}
