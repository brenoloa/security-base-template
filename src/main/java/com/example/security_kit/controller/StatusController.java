package com.example.security_kit.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/status")
@Tag(name = "Status", description = "Endpoint público para verificar a disponibilidade da aplicação")
public class StatusController {

    @GetMapping
    @Operation(summary = "Verifica se a API está online")
    public Map<String, Object> getStatus() {
        return Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now(),
            "message", "Security Kit API is running"
        );
    }
}
