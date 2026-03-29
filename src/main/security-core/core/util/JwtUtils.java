package core.util;

import core.constants.SecurityConstants;
import core.model.JwtPayload;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtUtils {

    @Value("${security.jwt.secret:default-secret-key-that-should-be-at-least-thirty-two-bytes-long}")
    private String secret;

    @Value("${security.jwt.expiration:#{3600}}")
    private Long expiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(JwtPayload payload) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(SecurityConstants.CLAIM_USER_ID, payload.getUserId());
        claims.put(SecurityConstants.CLAIM_ROLES, payload.getRoles());
        claims.put(SecurityConstants.CLAIM_EMAIL, payload.getEmail());
        
        if (payload.getOtherClaims() != null) {
            claims.putAll(payload.getOtherClaims());
        }

        return Jwts.builder()
                .claims(claims)
                .subject(payload.getSubject())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public JwtPayload extractPayload(String token) {
        Claims claims = extractAllClaims(token);
        
        Map<String, Object> otherClaims = new HashMap<>(claims);
        otherClaims.remove(SecurityConstants.CLAIM_USER_ID);
        otherClaims.remove(SecurityConstants.CLAIM_ROLES);
        otherClaims.remove(SecurityConstants.CLAIM_EMAIL);
        otherClaims.remove(Claims.SUBJECT);
        otherClaims.remove(Claims.ISSUED_AT);
        otherClaims.remove(Claims.EXPIRATION);

        return JwtPayload.builder()
                .subject(claims.getSubject())
                .userId((String) claims.get(SecurityConstants.CLAIM_USER_ID))
                .email((String) claims.get(SecurityConstants.CLAIM_EMAIL))
                .roles((List<String>) claims.get(SecurityConstants.CLAIM_ROLES))
                .otherClaims(otherClaims)
                .build();
    }
}
