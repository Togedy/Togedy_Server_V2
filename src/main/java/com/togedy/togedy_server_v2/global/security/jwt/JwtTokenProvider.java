package com.togedy.togedy_server_v2.global.security.jwt;

import com.togedy.togedy_server_v2.global.error.ErrorCode;
import com.togedy.togedy_server_v2.global.security.AuthUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
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
public class JwtTokenProvider {

    @Value("${jwt.secret-key}")
    private String JWT_SECRET_KEY;

    @Value("${jwt.access-expired-in}")
    private Long JWT_ACCESS_EXPIRED_IN;

    @Value("${jwt.refresh-expired-in}")
    private Long JWT_REFRESH_EXPIRED_IN;

    private Key key;

    public static final String BEARER = "Bearer ";

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET_KEY));
    }

    // 토큰 생성
    public JwtTokenInfo generateTokenInfo(Long userId, String email) {
        String accessToken = BEARER + createToken(userId, email, JWT_ACCESS_EXPIRED_IN);
        String refreshToken = BEARER + createToken(userId, email, JWT_REFRESH_EXPIRED_IN);
        return JwtTokenInfo.of(accessToken, refreshToken);
    }

    public String createToken(Long userId, String email, Long expireInMs) {
        Date now = new Date();

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
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
            throw new JwtException(ErrorCode.JWT_EXPIRED);
        } catch (UnsupportedJwtException e) {
            throw new JwtException(ErrorCode.JWT_UNSUPPORTED);
        } catch (MalformedJwtException e) {
            throw new JwtException(ErrorCode.JWT_MALFORMED);
        } catch (SignatureException e) {
            throw new JwtException(ErrorCode.JWT_INVALID_SIGNATURE);
        } catch (IllegalArgumentException e) {
            throw new JwtException(ErrorCode.JWT_INVALID);
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
        String email = claims.get("email", String.class);

        AuthUser authUser = AuthUser.builder()
                .id(userId)
                .email(email)
                .build();

        return new UsernamePasswordAuthenticationToken(authUser, null, null);
    }

    private String removeBearerPrefix(String token) {
        return token != null && token.startsWith(BEARER)
                ? token.substring(BEARER.length())
                : token;
    }
}
