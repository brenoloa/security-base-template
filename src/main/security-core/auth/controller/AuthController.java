package auth.controller;

import core.model.JwtPayload;
import core.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticacao", description = "Endpoints para emissao de token JWT")
public class AuthController {

    private final TokenService tokenService;

    @PostMapping("/login-mock")
    @Operation(summary = "Gera token JWT mock", description = "Gera um JWT a partir de userId, email e roles para testes e integracao.")
    public String login(@RequestBody LoginRequest request) {
        JwtPayload payload = JwtPayload.builder()
                .subject(request.getEmail())
                .userId(request.getUserId())
                .email(request.getEmail())
                .roles(request.getRoles())
                .build();
        
        return tokenService.generateToken(payload);
    }

    @Data
    public static class LoginRequest {
        private String userId;
        private String email;
        private List<String> roles;
    }
}
