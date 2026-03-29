# Security Starter Module

This module provides a production-ready security layer for Spring Boot applications using JWT and Spring Security.

## Features

- **JWT Authentication**: Validates Bearer tokens from the `Authorization` header.
- **RBAC Authorization**: Supports Role-Based Access Control using `@PreAuthorize` annotations.
- **User Injection**: Automatically injects the authenticated user into controller methods via `@UsuarioAtual`.
- **Stateless**: No session state is maintained; no database or JPA dependency.

## Architecture

The module is located in `src/main/security-core` and is organized as follows:

- `config`: Spring Security and MVC configuration.
- `constants`: Shared security constants.
- `exception`: Custom security exceptions.
- `filter`: `JwtAuthenticationFilter` for token interception.
- `model`: Data models (`UsuarioLogado`, `JwtPayload`).
- `resolver`: Argument resolver for `@UsuarioAtual`.
- `service`: `TokenService` for token operations.
- `util`: `JwtUtils` for JWT signing and parsing.

## Usage

### 1. HTTP Requests (IntelliJ IDEA)

Para testar os endpoints rapidamente, utilize os arquivos `.http` localizados em `src/main/resources/http`:

- **auth.http**: Contém requisições para o endpoint `/api/auth/login-mock`. Ao executar, o token retornado é salvo automaticamente na variável global `{{auth_token}}`.
- **test.http**: Contém requisições para os endpoints de teste (`/api/test/me`, `/api/test/admin`, etc.), utilizando o `{{auth_token}}` obtido anteriormente.

### 2. Configuration

Ensure your `application.properties` includes the JWT secret and expiration:

```properties
security.jwt.secret=your-long-secure-secret-key-at-least-32-bytes
security.jwt.expiration=3600
```

### 2. Injecting the Authenticated User

Use the `@UsuarioAtual` annotation in your controllers:

```java
@GetMapping("/me")
public UsuarioLogado getMe(@UsuarioAtual UsuarioLogado user) {
    return user;
}
```

### 3. Role-Based Access Control

Use `@PreAuthorize` with roles extracted from the JWT (prefixed with `ROLE_` automatically):

```java
@GetMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public String adminOnly() {
    return "Admin access granted";
}
```

## Security Flow

1. User sends a request with `Authorization: Bearer <token>`.
2. `JwtAuthenticationFilter` intercepts the request, validates the token, and extracts claims.
3. If valid, it populates the `SecurityContext` with a `UsuarioLogado` principal and authorities.
4. `UsuarioLogadoResolver` detects the `@UsuarioAtual` annotation and injects the `UsuarioLogado` object from the `SecurityContext` into the controller method.
