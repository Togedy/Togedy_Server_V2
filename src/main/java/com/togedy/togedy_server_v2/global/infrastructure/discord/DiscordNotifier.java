package com.togedy.togedy_server_v2.global.infrastructure.discord;

import com.togedy.togedy_server_v2.global.logging.MdcLoggingFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class DiscordNotifier {

    private static final int MAX_CONTENT_LENGTH = 2000;
    private static final int STACK_TRACE_DEPTH = 5;
    private static final Duration TIMEOUT = Duration.ofSeconds(3);

    private final WebClient webClient;
    private final String webhookUrl;

    public DiscordNotifier(
            WebClient.Builder builder,
            @Value("${discord.webhook.url:}") String webhookUrl
    ) {
        this.webClient = builder.build();
        this.webhookUrl = webhookUrl;
    }

    public void notify(String title, Exception e) {
        if (!StringUtils.hasText(webhookUrl)) {
            return;
        }

        webClient.post()
                .uri(webhookUrl)
                .bodyValue(Map.of("content", buildContent(title, e)))
                .retrieve()
                .toBodilessEntity()
                .timeout(TIMEOUT)
                .subscribe(
                        response -> {
                        },
                        error -> log.warn("Discord 알림 전송 실패: {}", error.getMessage())
                );
    }

    private String buildContent(String title, Exception e) {
        String content = """
                🚨 **[%s] %s**
                - request: `%s`
                - requestId: `%s`
                - userId: `%s`
                - message: %s
                ```
                %s
                ```"""
                .formatted(
                        title,
                        e.getClass().getSimpleName(),
                        resolveRequest(),
                        Objects.requireNonNullElse(MDC.get(MdcLoggingFilter.REQUEST_ID), "-"),
                        Objects.requireNonNullElse(MDC.get(MdcLoggingFilter.USER_ID), "-"),
                        e.getMessage(),
                        resolveStackTrace(e)
                );

        if (content.length() > MAX_CONTENT_LENGTH) {
            return content.substring(0, MAX_CONTENT_LENGTH - 3) + "```";
        }
        return content;
    }

    private String resolveRequest() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            HttpServletRequest request = attributes.getRequest();
            return request.getMethod() + " " + request.getRequestURI();
        }
        return "-";
    }

    private String resolveStackTrace(Exception e) {
        return Arrays.stream(e.getStackTrace())
                .limit(STACK_TRACE_DEPTH)
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));
    }
}
