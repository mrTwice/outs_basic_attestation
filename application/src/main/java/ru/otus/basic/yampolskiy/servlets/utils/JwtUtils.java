package ru.otus.basic.yampolskiy.servlets.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtils {
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode("0KMg0LvRg9C60L7QvNC+0YDRjNGPINC00YPQsSDQt9C10LvRkdC90YvQuTsK0JfQu9Cw0YLQsNGPINGG0LXQv9GMINC90LAg0LTRg9Cx0LUg0YLQvtC8OgrQmCDQtNC90ZHQvCDQuCDQvdC+0YfRjNGOINC60L7RgiDRg9GH0ZHQvdGL0LkK0JLRgdGRINGF0L7QtNC40YIg0L/QviDRhtC10L/QuCDQutGA0YPQs9C+0Lw7"));
    private static final long EXPIRATION_TIME_MS = 86400000; // 1 day

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

