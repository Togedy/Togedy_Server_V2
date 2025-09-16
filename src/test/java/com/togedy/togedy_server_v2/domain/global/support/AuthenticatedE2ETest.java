package com.togedy.togedy_server_v2.domain.global.support;

import com.togedy.togedy_server_v2.domain.global.fixtures.AuthUserFixture;
import com.togedy.togedy_server_v2.global.security.AuthUser;
import com.togedy.togedy_server_v2.global.security.jwt.JwtTokenProvider;
import com.togedy.togedy_server_v2.global.service.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class AuthenticatedE2ETest extends AbstractE2ETest {

    @MockitoBean
    protected JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    protected S3Service s3Service;

    protected static RequestPostProcessor bearer(String token) {
        return request -> {
            request.addHeader("Authorization", "Bearer " + token);
            return request;
        };
    }

    @BeforeEach
    void authSetUp() {
        when(jwtTokenProvider.removeBearerPrefix(anyString()))
                .thenAnswer(invocation -> {
                    String header = (String) invocation.getArgument(0);
                    return header != null && header.startsWith("Bearer ")
                            ? header.substring("Bearer ".length())
                            : header;
                });

        when(jwtTokenProvider.validateToken(anyString())).thenReturn(true);

        AuthUser principal = AuthUserFixture.createAuthUser(1L);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());
        when(jwtTokenProvider.getAuthentication(anyString())).thenReturn(authentication);

        when(s3Service.uploadFile(any(MultipartFile.class)))
                .thenReturn("https://mock-s3/test.png");
    }
}
