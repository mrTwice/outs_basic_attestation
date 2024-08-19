package ru.otus.basic.yampolskiy.servlets.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import ru.otus.basic.yampolskiy.webserver.ConfigLoader;

import java.security.Key;
import java.util.Date;

public class JwtUtils extends ConfigLoader {
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(getProperty("jwt.secret.key")));
    private static final long EXPIRATION_TIME_MS = getIntProperty("jwt.expiration.time",86400000); // 1 day

    public static String createToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
                .signWith(SECRET_KEY)
                .compact();
    }

    public static boolean isValidToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static String extractUsername(String token) {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
    }
}

