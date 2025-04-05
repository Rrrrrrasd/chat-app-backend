package com.example.backend.common.util;

import com.example.backend.common.exception.CustomException;
import com.example.backend.common.exception.CustomExceptionEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final Key key;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtUtil(@Value("${jwt.secret}") String secretKey,
                   //${jwt.access}
                   @Value("${jwt.access}") long accessTokenExpiration,
                   @Value("${jwt.refresh}") long refreshTokenExpiration) {
        byte[] decodeKey = Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(decodeKey);
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }




    // Access Token 발급
    public String generateAccessToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Refresh Token 발급
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 검증
    public void validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            log.error("JWT 만료됨: {}", e.getMessage());
            throw new CustomException(CustomExceptionEnum.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 형식: {}", e.getMessage());
            throw new CustomException(CustomExceptionEnum.UNSUPPORTED_TOKEN);
        } catch (MalformedJwtException e) {
            log.error("잘못된 JWT 형식: {}", e.getMessage());
            throw new CustomException(CustomExceptionEnum.MALFORMED_TOKEN);
        } catch (SignatureException e) {
            log.error("JWT 서명 검증 실패: {}", e.getMessage());
            throw new CustomException(CustomExceptionEnum.INVALID_SIGNATURE);
        } catch (Exception e) {
            log.error("JWT 검증 실패: {}", e.getMessage());
            throw new CustomException(CustomExceptionEnum.JWT_VALIDATION_FAILED);
        }
    }

    // 토큰에서 사용자 이름 추출
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // 추가분
    public long getExpiration(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getExpiration().getTime() / 1000;
    }
}
