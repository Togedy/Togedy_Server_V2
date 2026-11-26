package com.togedy.togedy_server_v2.domain.badge.handler;

import com.togedy.togedy_server_v2.domain.badge.application.BadgeGrantService;
import com.togedy.togedy_server_v2.domain.planner.event.TimerStoppedEvent;
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
public class TimerStoppedEventHandler {

    private final BadgeGrantService badgeGrantService;

    /**
     * 타이머 종료가 커밋된 뒤 한우물왕과 월간 파이터 뱃지를 판정한다.
     * <p>
     * 이미 커밋된 종료 트랜잭션에 참여하지 않도록 트랜잭션 없이 실행해, 뱃지 저장마다 개별 트랜잭션을 사용한다.
     * 뱃지별로 실패를 격리하며, 지급 실패는 타이머 종료에 영향을 주지 않는다.
     * </p>
     *
     * @param event 타이머 종료 이벤트
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void handle(TimerStoppedEvent event) {
        LocalDate studyDate = TimeUtil.startOfStudyDay(event.endTime()).toLocalDate();

        try {
            badgeGrantService.grantOneSubjectBadges(event.userId(), event.studySubjectId(), studyDate);
        } catch (Exception e) {
            log.error("한우물왕 뱃지 지급 실패 userId={}", event.userId(), e);
        }

        try {
            badgeGrantService.grantMonthlyBadge(event.userId(), studyDate);
        } catch (Exception e) {
            log.error("월간 파이터 뱃지 지급 실패 userId={}", event.userId(), e);
        }
    }
}
