package com.gaurav.taskmanager2.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtil {

    //getting Key for sing in
    private SecretKey getSignInKey() {
        //secret key
        String secret_key = "my-super-secret-key-that-is-long-enough-1234567890!~@#$";
        return Keys.hmacShaKeyFor(secret_key.getBytes(StandardCharsets.UTF_8));
    }
    //generating the token
    public String generateToken(UserDetails userDetails) {
        // one hour time
        long expirationTime = 1000 * 60 * 60;
        Map<String, Object> claims = new HashMap<>();
        claims.put("username",userDetails.getUsername());
        claims.put("role",userDetails.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()));
        System.out.println(claims);
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignInKey())
                .compact();
    }
    //extracting all claims
    private Claims extractingAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    //extracting username
    public String extractUsername(String token) {
        return extractingAllClaims(token).getSubject();
    }
    //extracting expiration date
    // private Date extractExpirationDate(String token) {
    //     return extractingAllClaims(token).getExpiration();
    // }
    //check token expiration
    private Boolean isTokenExpired(String token) {
        return extractingAllClaims(token).getExpiration().before(new Date());
    }
    //validate the token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
