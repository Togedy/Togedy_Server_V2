package com.togedy.togedy_server_v2.global.logging;

import com.togedy.togedy_server_v2.global.infrastructure.discord.DiscordNotifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class SchedulerErrorAspect {

    private final DiscordNotifier discordNotifier;

    @AfterThrowing(
            pointcut = "@annotation(org.springframework.scheduling.annotation.Scheduled)",
            throwing = "e"
    )
    public void notifySchedulerError(JoinPoint joinPoint, Exception e) {
        String job = joinPoint.getSignature().getDeclaringType().getSimpleName()
                + "." + joinPoint.getSignature().getName();

        log.error("[SCHEDULER] {} 실패: {}", job, e.getMessage());
        discordNotifier.notify("SCHEDULER " + job, e);
    }
}
