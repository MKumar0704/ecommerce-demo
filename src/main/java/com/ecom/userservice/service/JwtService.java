package com.ecom.userservice.service;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.security.auth.login.CredentialNotFoundException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Logger;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private String jwtExpirationMs;

    public String generateToken(String username, UUID userId){
        return Jwts.builder()
                .subject(userId.toString())
                .claim("username",username)
                .claim("roles","USER")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + Long.parseLong(jwtExpirationMs) ))
                .signWith(getSignInKey())
                .compact();
    }

    public String extractUsername(String token){
        return extractClaim(token,claims ->
            claims.get("username").toString()
        );
    }

    public String extractUserId(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = parseToken(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(String token){
        return extractClaim(token,Claims::getExpiration).after(new Date());
    }


    private Claims parseToken(String token){
        try{
            return Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (SignatureException ex) {
            throw new SignatureException("Invalid Signature");
        }catch (ExpiredJwtException ex) {
            throw new ExpiredJwtException(ex.getHeader(),ex.getClaims(),"Token Expired");
        }
        catch (JwtException ex) {
            throw new JwtException("Issue with the token",ex.getCause());
        }

    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
