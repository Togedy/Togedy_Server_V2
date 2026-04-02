package com.togedy.togedy_server_v2.domain.chat.dao;

import com.togedy.togedy_server_v2.domain.chat.entity.ChatMessage;
import com.togedy.togedy_server_v2.domain.chat.enums.Sender;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    Optional<ChatMessage> findTopByUserIdAndSenderOrderByCreatedAtDesc(
            Long userId,
            Sender sender
    );

    void deleteAllByUserId(Long userId);
}
