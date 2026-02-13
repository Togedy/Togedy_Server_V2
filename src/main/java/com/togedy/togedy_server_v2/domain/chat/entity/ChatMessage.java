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

    private Long userId;

    private String content;

    private Sender sender;

    private NerKeyword nerKeyword;

    @Builder
    public ChatMessage(Long userId, String content, Sender sender, NerKeyword nerKeyword) {
        this.userId = userId;
        this.content = content;
        this.sender = sender;
        this.nerKeyword = nerKeyword;
    }
}
