package com.example.security_kit.controller;

import core.model.UsuarioLogado;
import core.resolver.UsuarioAtual;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@Tag(name = "Testes de Seguranca", description = "Endpoints protegidos para validar autenticacao e autorizacao")
@SecurityRequirement(name = "bearerAuth")
public class TestController {

    @GetMapping("/me")
    @Operation(summary = "Retorna usuario autenticado", description = "Exemplo de uso do @UsuarioAtual para receber o usuario decodificado do token.")
    public UsuarioLogado getMe(@UsuarioAtual UsuarioLogado user) {
        return user;
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Endpoint restrito a ADMIN")
    public String adminOnly() {
        return "Admin access granted";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Endpoint restrito a USER")
    public String userOnly() {
        return "User access granted";
    }
}
