package com.togedy.togedy_server_v2.global.infrastructure.kakao;

import com.togedy.togedy_server_v2.domain.user.dto.KakaoUserInfoResponse;
import com.togedy.togedy_server_v2.domain.user.exception.auth.InvalidKakaoTokenException;
import com.togedy.togedy_server_v2.domain.user.exception.auth.KakaoApiErrorException;
import com.togedy.togedy_server_v2.domain.user.exception.auth.KakaoTokenExpiredException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


@Component
@RequiredArgsConstructor
public class KakaoApiClient {

    private final RestTemplate restTemplate;

    public KakaoUserInfoResponse getUserInfo(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<KakaoUserInfoResponse> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.GET,
                    entity,
                    KakaoUserInfoResponse.class
            );
            return response.getBody();
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new KakaoTokenExpiredException();
        } catch (HttpClientErrorException.Forbidden e) {
            throw new InvalidKakaoTokenException();
        } catch (RestClientException e) {
            throw new KakaoApiErrorException();
        }

    }
}
