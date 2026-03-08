package com.example.clothing.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    // 使用足够长的安全密钥
    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 生成token
    public String generateToken(String username) {

        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1天
                .signWith(secretKey)
                .compact();
    }

    // 解析token
    public String parseToken(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
}
