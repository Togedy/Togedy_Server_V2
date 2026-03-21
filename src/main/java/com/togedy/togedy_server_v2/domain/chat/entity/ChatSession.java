package com.togedy.togedy_server_v2.domain.chat.entity;

import com.togedy.togedy_server_v2.global.entity.BaseDocument;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "chat_session")
public class ChatSession extends BaseDocument {

    @Id
    private String id;

    private Long userId;

    @Builder
    public ChatSession(Long userId) {
        this.userId = userId;
    }

}
