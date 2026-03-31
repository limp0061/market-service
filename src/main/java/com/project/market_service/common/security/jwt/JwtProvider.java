package com.project.market_service.common.security.jwt;

import static com.project.market_service.common.constants.AuthConstants.CLAIM_LOGIN_ID;
import static com.project.market_service.common.constants.AuthConstants.CLAIM_ROLE;

import com.project.market_service.auth.exception.AuthErrorCode;
import com.project.market_service.common.exception.UnAuthorizationException;
import com.project.market_service.user.domain.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final long accessExpiration;
    private final long refreshExpiration;
    private final String issuer;

    public JwtProvider(
            JwtProperties jwtProperties,
            @Value("${spring.application.name}") String issuer
    ) {
        this.secretKey = jwtProperties.getSecretKey();
        this.accessExpiration = jwtProperties.accessExpiration();
        this.refreshExpiration = jwtProperties.refreshExpiration();
        this.issuer = issuer;
    }

    public String createAccessToken(Long userId, String loginId, UserRole userRole) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim(CLAIM_LOGIN_ID, loginId)
                .claim(CLAIM_ROLE, userRole.getCode())
                .issuer(issuer)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(Long userId) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuer(issuer)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(secretKey)
                .compact();
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public JwtUserInfo getUserInfo(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            String subject = claims.getSubject();
            String loginId = claims.get(CLAIM_LOGIN_ID, String.class);
            String role = claims.get(CLAIM_ROLE, String.class);

            if (subject == null || role == null || role.isBlank()) {
                throw new UnAuthorizationException(AuthErrorCode.INVALID_TOKEN);
            }

            return new JwtUserInfo(
                    Long.parseLong(subject),
                    loginId,
                    role
            );
        } catch (ExpiredJwtException e) {
            throw new UnAuthorizationException(AuthErrorCode.TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnAuthorizationException(AuthErrorCode.INVALID_TOKEN);
        }
    }

    public Long getUserId(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            String subject = claims.getSubject();

            if (subject == null) {
                throw new UnAuthorizationException(AuthErrorCode.INVALID_TOKEN);
            }

            return Long.parseLong(subject);
        } catch (ExpiredJwtException e) {
            throw new UnAuthorizationException(AuthErrorCode.TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnAuthorizationException(AuthErrorCode.INVALID_TOKEN);
        }
    }

    public long remainExpiration(String token) {
        Instant expiration = getClaimsFromToken(token).getExpiration().toInstant();
        return Math.max(expiration.toEpochMilli() - System.currentTimeMillis(), 0);
    }
}
