package com.togedy.togedy_server_v2.domain.badge.handler;

import com.togedy.togedy_server_v2.domain.badge.application.BadgeGrantService;
import com.togedy.togedy_server_v2.domain.planner.event.TimerStartedEvent;
import com.togedy.togedy_server_v2.global.util.TimeUtil;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimerStartedEventHandler {

    private final BadgeGrantService badgeGrantService;

    /**
     * 타이머 시작이 커밋된 뒤 복귀 뱃지를 판정한다.
     * <p>
     * 이미 커밋된 시작 트랜잭션에 참여하지 않도록 트랜잭션 없이 실행하며, 지급 실패는 타이머 시작에 영향을 주지 않는다.
     * </p>
     *
     * @param event 타이머 시작 이벤트
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void handle(TimerStartedEvent event) {
        LocalDate studyDate = TimeUtil.startOfStudyDay(event.startTime()).toLocalDate();

        try {
            badgeGrantService.grantComebackBadge(event.userId(), studyDate);
        } catch (Exception e) {
            log.error("복귀 뱃지 지급 실패 userId={}", event.userId(), e);
        }
    }
}
