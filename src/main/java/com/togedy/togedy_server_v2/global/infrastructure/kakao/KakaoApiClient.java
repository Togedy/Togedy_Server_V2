package com.togedy.togedy_server_v2.global.infrastructure.kakao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.togedy.togedy_server_v2.domain.user.dto.KakaoUserInfoResponse;
import com.togedy.togedy_server_v2.domain.user.exception.auth.InvalidKakaoTokenException;
import com.togedy.togedy_server_v2.domain.user.exception.auth.KakaoApiErrorException;
import com.togedy.togedy_server_v2.domain.user.exception.auth.KakaoTokenExpiredException;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


@Component
@Slf4j
@RequiredArgsConstructor
public class KakaoApiClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kakao.api.url.user-info}")
    private String kakaoUserInfoUrl;

    public KakaoUserInfoResponse getUserInfo(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<KakaoUserInfoResponse> response = restTemplate.exchange(
                    kakaoUserInfoUrl,
                    HttpMethod.GET,
                    entity,
                    KakaoUserInfoResponse.class
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw mapKakaoClientException(e);
        } catch (RestClientException e) {
            throw new KakaoApiErrorException();
        }
    }

    private RuntimeException mapKakaoClientException(HttpClientErrorException e) {
        HttpStatusCode statusCode = e.getStatusCode();
        String responseBody = e.getResponseBodyAsString();
        KakaoErrorPayload errorPayload = parseErrorPayload(responseBody);

        if (statusCode == HttpStatus.UNAUTHORIZED || statusCode == HttpStatus.FORBIDDEN) {
            if (isExpiredTokenError(errorPayload)) {
                return new KakaoTokenExpiredException();
            }
            return new InvalidKakaoTokenException();
        }

        return new KakaoApiErrorException();
    }

    private KakaoErrorPayload parseErrorPayload(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            return new KakaoErrorPayload(null, null);
        }

        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String code = root.path("code").isMissingNode() ? null : root.path("code").asText(null);
            String message = root.path("msg").isMissingNode() ? null : root.path("msg").asText(null);
            return new KakaoErrorPayload(code, message);
        } catch (Exception e) {
            log.warn("Failed to parse Kakao error response: {}", responseBody, e);
            return new KakaoErrorPayload(null, null);
        }
    }

    private boolean isExpiredTokenError(KakaoErrorPayload errorPayload) {
        if (errorPayload.message() == null || errorPayload.message().isBlank()) {
            return false;
        }

        String normalized = errorPayload.message().toLowerCase(Locale.ROOT);
        return normalized.contains("expired")
                || normalized.contains("token has expired")
                || normalized.contains("expired token");
    }

    private record KakaoErrorPayload(String code, String message) {
    }
}
