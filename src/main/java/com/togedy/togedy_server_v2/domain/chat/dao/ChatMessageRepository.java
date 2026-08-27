package com.togedy.togedy_server_v2.domain.chat.dao;

import com.togedy.togedy_server_v2.domain.chat.entity.ChatMessage;
import com.togedy.togedy_server_v2.domain.chat.enums.Sender;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    Optional<ChatMessage> findTopByUserIdAndSenderOrderByCreatedAtDesc(
            Long userId,
            Sender sender
    );

    void deleteAllByUserId(Long userId);

    @Query("{ 'userId': ?0, 'sender': ?1, 'createdAt': { $gte: ?2, $lt: ?3 } }")
    long countTodayChat(
            Long userId,
            Sender sender,
            LocalDateTime start,
            LocalDateTime end
    );
}
