package core.service;

import core.model.JwtPayload;
import core.util.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtUtils jwtUtils;

    public String generateToken(JwtPayload payload) {
        return jwtUtils.generateToken(payload);
    }

    public boolean validateToken(String token) {
        return jwtUtils.validateToken(token);
    }

    public Claims extractClaims(String token) {
        return jwtUtils.extractAllClaims(token);
    }
    
    public JwtPayload extractPayload(String token) {
        return jwtUtils.extractPayload(token);
    }
}
