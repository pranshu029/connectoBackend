package com.connectoBackend.security.service.impl;

import com.connectoBackend.common.exception.ForbiddenException;
import com.connectoBackend.common.exception.InvalidTokenException;
import com.connectoBackend.security.constant.JwtConstants;
import com.connectoBackend.security.dto.JwtClaims;
import com.connectoBackend.security.enums.TokenType;
import com.connectoBackend.security.service.JwtService;
import com.connectoBackend.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${application.security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private SecretKey signingKey;

    @PostConstruct
    private void initializeSigningKey() {

        signingKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secretKey)
        );
    }

    @Override
    public String generateAccessToken(User user) {

        return buildToken(user, TokenType.ACCESS, accessTokenExpiration);
    }

    @Override
    public String generateRefreshToken(User user) {

        return buildToken(user, TokenType.REFRESH, refreshTokenExpiration);
    }

    @Override
    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public UUID extractUserId(String token) {

        return UUID.fromString(
                extractClaim(
                        token,
                        claims -> claims.get(JwtConstants.USER_ID, String.class)
                )
        );
    }

    @Override
    public TokenType extractTokenType(String token) {

        return TokenType.valueOf(
                extractClaim(
                        token,
                        claims -> claims.get(JwtConstants.TOKEN_TYPE, String.class)
                )
        );
    }

    @Override
    public JwtClaims extractClaims(String token) {

        Claims claims = extractAllClaims(token);

        return JwtClaims.builder()
                .userId(UUID.fromString(claims.get(JwtConstants.USER_ID, String.class)))
                .email(claims.getSubject())
                .username(claims.get(JwtConstants.USERNAME, String.class))
                .role(claims.get(JwtConstants.ROLE, String.class))
                .tokenType(TokenType.valueOf(claims.get(JwtConstants.TOKEN_TYPE, String.class)))
                .build();
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {

        return extractUsername(token).equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    @Override
    public boolean isTokenExpired(String token) {

        return extractClaim(
                token,
                claims -> claims.getExpiration().before(new Date())
        );
    }

    // ------------------------------------------------------------------------
    // Private Helper Methods
    // ------------------------------------------------------------------------

    // -> Build JWT token.
    private String buildToken(
            User user,
            TokenType tokenType,
            long expiration
    ) {

        Date now = new Date();

        return Jwts.builder()
                .claims(buildClaims(user, tokenType))
                .subject(user.getEmail())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .signWith(signingKey)
                .compact();
    }

    // -> Build custom JWT claims.
    private Map<String, Object> buildClaims(
            User user,
            TokenType tokenType
    ) {

        Map<String, Object> claims = new HashMap<>();

        claims.put(JwtConstants.USER_ID, user.getId().toString());
        claims.put(JwtConstants.USERNAME, user.getUsername());
        claims.put(JwtConstants.ROLE, user.getRole().name());
        claims.put(JwtConstants.TOKEN_TYPE, tokenType.name());

        return claims;
    }

    // -> Extract a specific claim.
    private <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver
    ) {

        return claimsResolver.apply(extractAllClaims(token));
    }

    // -> Extract all claims from JWT token.
    private Claims extractAllClaims(String token) {

        try {

            return Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (JwtException | IllegalArgumentException exception) {

            throw new ForbiddenException("Invalid or expired JWT token.");
        }
    }
}