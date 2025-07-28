package com.togedy.togedy_server_v2.global.security.jwt;

import com.togedy.togedy_server_v2.domain.user.application.CustomUserDetailsService;
import com.togedy.togedy_server_v2.global.security.jwt.exception.*;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret-key}")
    private String JWT_SECRET_KEY;

    @Value("${jwt.access-expired-in}")
    private Long JWT_ACCESS_EXPIRED_IN;

    @Value("${jwt.refresh-expired-in}")
    private Long JWT_REFRESH_EXPIRED_IN;

    private Key key;

    public static final String BEARER = "Bearer ";

    private final CustomUserDetailsService userDetailsService;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET_KEY));
    }

    // 토큰 생성
    public JwtTokenInfo generateTokenInfo(Long userId) {
        String accessToken = BEARER + createToken(userId, JWT_ACCESS_EXPIRED_IN);
        String refreshToken = BEARER + createToken(userId, JWT_REFRESH_EXPIRED_IN);
        return JwtTokenInfo.of(accessToken, refreshToken);
    }

    public String createToken(Long userId, Long expireInMs) {
        Date now = new Date();

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expireInMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            token = removeBearerPrefix(token);
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException();
        } catch (UnsupportedJwtException e) {
            throw new JwtUnsupportedException();
        } catch (MalformedJwtException e) {
            throw new JwtMalformedException();
        } catch (SignatureException e) {
            throw new JwtInvalidSignatureException();
        } catch (IllegalArgumentException e) {
            throw new JwtInvalidException();
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Authentication getAuthentication(String token) {
        token = removeBearerPrefix(token);
        Claims claims = parseClaims(token);

        Long userId = Long.parseLong(claims.getSubject());

        UserDetails userDetails = userDetailsService.loadUserByUsername(String.valueOf(userId));

        return new UsernamePasswordAuthenticationToken(userDetails, null, null);
    }

    public String removeBearerPrefix(String token) {
        if (token == null) {
            return null;
        }

        if (token.startsWith(BEARER)) {
            return token.substring(BEARER.length());
        }

        return token;
    }
}
