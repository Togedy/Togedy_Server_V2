package com.togedy.togedy_server_v2.domain.chat.application;

import com.togedy.togedy_server_v2.domain.chat.dao.ChatMessageRepository;
import com.togedy.togedy_server_v2.domain.chat.dao.ChatSessionRepository;
import com.togedy.togedy_server_v2.domain.chat.dto.ai.AiAnswerResponse;
import com.togedy.togedy_server_v2.domain.chat.dto.ai.AiQuestionRequest;
import com.togedy.togedy_server_v2.domain.chat.dto.client.PostQuestionRequest;
import com.togedy.togedy_server_v2.domain.chat.dto.client.PostQuestionResponse;
import com.togedy.togedy_server_v2.domain.chat.entity.ChatMessage;
import com.togedy.togedy_server_v2.domain.chat.entity.NerKeyword;
import com.togedy.togedy_server_v2.domain.chat.enums.Sender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final AiChatClient aiChatClient;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public PostQuestionResponse handleQuestion(PostQuestionRequest request, Long userId) {
        String question = request.getQuestion();
        String followUpAnswer = request.getFollowUpAnswer();
        boolean isFirst = determineFirst(followUpAnswer);

        ChatMessage sendMessage;
        NerKeyword nerKeyword;
        AiQuestionRequest aiQuestionRequest;

        // NER 조회 후 요청 생성
        if (isFirst) {
            sendMessage = ChatMessage.builder()
                    .userId(userId)
                    .content(question)
                    .sender(Sender.USER)
                    .build();

            aiQuestionRequest = AiQuestionRequest.of(question);
        } else {
            ChatMessage previousChatMessage = chatMessageRepository.findTopByUserIdAndSenderOrderByCreatedAtDesc(
                    userId,
                    Sender.USER
            ).orElseThrow(RuntimeException::new);

            nerKeyword = previousChatMessage.getNerKeyword();

            sendMessage = ChatMessage.builder()
                    .userId(userId)
                    .content(followUpAnswer)
                    .sender(Sender.USER)
                    .nerKeyword(nerKeyword)
                    .build();

            aiQuestionRequest = AiQuestionRequest.of(question, followUpAnswer, nerKeyword);
        }

        chatMessageRepository.save(sendMessage);

        // WebClient 요청 전송
        AiAnswerResponse aiAnswer = aiChatClient.requestQuestion(aiQuestionRequest)
                .block();

        if (aiAnswer == null) {
            throw new RuntimeException();
        }

        ChatMessage answerMessage = ChatMessage.builder()
                .userId(userId)
                .content(aiAnswer.getAnswer())
                .sender(Sender.AI)
                .nerKeyword(aiAnswer.getNerKeyword())
                .build();

        chatMessageRepository.save(answerMessage);

        return PostQuestionResponse.of(aiAnswer);
    }

    private boolean determineFirst(String followUpAnswer) {
        return followUpAnswer == null || followUpAnswer.isBlank();
    }

}
