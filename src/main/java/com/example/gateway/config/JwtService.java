package com.example.gateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    public Boolean parseToken(String token) {
        try {
            Claims claims = Jwts
                    .parser()
                    .setSigningKey(secret.getBytes())
                    .parseClaimsJws(token)
                    .getBody();

            TokenInfo.builder()
                    .id(UUID.fromString(claims.get("id").toString()))
                    .name(claims.get("name").toString())
                    .number(claims.get("number").toString())
                    .role(claims.get("role").toString())
                    .build();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
