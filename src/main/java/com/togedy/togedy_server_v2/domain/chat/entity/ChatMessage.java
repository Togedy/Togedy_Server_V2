package com.togedy.togedy_server_v2.domain.chat.entity;

import com.togedy.togedy_server_v2.domain.chat.enums.Sender;
import com.togedy.togedy_server_v2.global.entity.BaseDocument;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "chat_message")
public class ChatMessage extends BaseDocument {

    @Id
    private String id;

    private Long chatSessionId;

    private String content;

    private Sender sender;

    @Builder
    public ChatMessage(Long chatSessionId, String content, Sender sender) {
        this.chatSessionId = chatSessionId;
        this.content = content;
        this.sender = sender;
    }
}
