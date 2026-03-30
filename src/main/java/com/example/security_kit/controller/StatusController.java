package com.example.security_kit.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/status")
@Tag(name = "Status", description = "Endpoint publico para verificar a disponibilidade da aplicacao")
public class StatusController {

    private static final String UNKNOWN = "UNKNOWN";
    private static final DateTimeFormatter STATUS_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final BuildProperties buildProperties;
    private final RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

    public StatusController(ObjectProvider<BuildProperties> buildPropertiesProvider) {
        this.buildProperties = buildPropertiesProvider.getIfAvailable();
    }

    @GetMapping
    @Operation(summary = "Verifica se a API esta online")
    public Map<String, Object> getStatus() {
        Instant startedAt = Instant.ofEpochMilli(runtimeMXBean.getStartTime());
        long uptimeSeconds = runtimeMXBean.getUptime() / 1000;

        Map<String, Object> versions = new LinkedHashMap<>();
        versions.put("app", getBuildVersion());
        versions.put("springBoot", SpringBootVersion.getVersion());
        versions.put("java", System.getProperty("java.runtime.version"));
        versions.put("gradle", getGradleVersion());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "OK");
        response.put("startedAt", formatDate(startedAt));
        response.put("uptimeSeconds", uptimeSeconds);
        response.put("message", "Security Kit API is running");
        response.put("versions", versions);

        return response;
    }

    private String getBuildVersion() {
        return buildProperties != null ? buildProperties.getVersion() : UNKNOWN;
    }

    private String getGradleVersion() {
        if (buildProperties == null) {
            return UNKNOWN;
        }
        String value = buildProperties.get("gradle.version");
        return value != null ? value : UNKNOWN;
    }

    private String formatDate(Instant instant) {
        return instant.atZone(ZoneId.systemDefault()).format(STATUS_DATE_FORMATTER);
    }
}
