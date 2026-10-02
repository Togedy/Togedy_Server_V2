package com.togedy.togedy_server_v2.global.handler;

import com.togedy.togedy_server_v2.global.event.ImageRemovedEvent;
import com.togedy.togedy_server_v2.global.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageRemoveEventHandler {

    private final S3Service s3Service;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ImageRemovedEvent event) {
        try {
            s3Service.deleteFile(event.imageUrl());
        } catch (Exception e) {
            log.error("S3 이미지 삭제 실패: {}", event.imageUrl(), e);
        }
    }

}
