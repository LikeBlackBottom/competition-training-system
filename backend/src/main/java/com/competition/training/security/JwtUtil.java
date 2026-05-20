package com.competition.training.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    @Value("${jwt.secret:competition-training-secret-key-change-me-please-keep-long}")
    private String secret;
    @Value("${jwt.expire-hours:24}")
    private long expireHours;

    public String createAdminToken(Long adminId, String username, String role) {
        return createToken(Map.of("role", "ADMIN", "adminId", adminId, "username", username, "adminRole", role));
    }

    public String createTeamToken(Long teamId) {
        return createToken(Map.of("role", "TEAM", "teamId", teamId));
    }

    public LoginUser parse(String token) {
        Claims c = Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();
        String role = c.get("role", String.class);
        Long adminId = toLong(c.get("adminId"));
        Long teamId = toLong(c.get("teamId"));
        String username = c.get("username", String.class);
        return new LoginUser(role, adminId, teamId, username);
    }

    private String createToken(Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expireHours * 3600)))
                .signWith(key())
                .compact();
    }

    private SecretKey key() {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            bytes = (secret + "00000000000000000000000000000000").getBytes(StandardCharsets.UTF_8);
        }
        return Keys.hmacShaKeyFor(bytes);
    }

    private Long toLong(Object value) {
        if (value == null) return null;
        if (value instanceof Number n) return n.longValue();
        return Long.valueOf(value.toString());
    }
}
