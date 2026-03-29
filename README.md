# Módulo de Segurança JWT para Spring Boot

Este projeto entrega uma camada de segurança pronta para uso com **JWT + Spring Security**, incluindo autenticação stateless, autorização por papéis e injeção do usuário autenticado no controller.

## O que o sistema faz

- Valida tokens JWT enviados no header `Authorization: Bearer <token>`.
- Converte roles do token para authorities do Spring (`ROLE_ADMIN`, `ROLE_USER`, etc.).
- Protege endpoints automaticamente (qualquer rota fora de `/api/auth/**` exige autenticação).
- Permite autorização por perfil com `@PreAuthorize`.
- Injeta o usuário autenticado no controller com `@UsuarioAtual`.

## Estrutura do módulo

O núcleo está em `src/main/security-core`:

- `core/config`
  - `SecurityConfig`: define segurança stateless, rotas públicas e registra o filtro JWT.
  - `JwtConfig`: registra o resolver de argumento para `@UsuarioAtual`.
- `core/filter`
  - `JwtAuthenticationFilter`: lê e valida o token, monta `UsuarioLogado` e popula o `SecurityContext`.
- `core/service`
  - `TokenService`: fachada para gerar/validar/extrair payload do token.
- `core/util`
  - `JwtUtils`: assinatura, parsing e validação do JWT.
- `core/resolver`
  - `UsuarioAtual` + `UsuarioLogadoResolver`: permite usar `@UsuarioAtual` no método do controller.
- `auth/controller`
  - `AuthController`: endpoint mock para gerar token (`/api/auth/login-mock`).

## Configuração mínima

No `application.properties`, configure:

```properties
security.jwt.secret=sua-chave-secreta-forte-com-no-minimo-32-bytes
security.jwt.expiration=3600
```

> `security.jwt.expiration` está em segundos.

## Como usar no dia a dia

### 1) Subir a aplicação

```bash
./gradlew bootRun
```

### 2) Gerar token

Use o arquivo `src/main/resources/http/auth.http` (IntelliJ HTTP Client) ou faça POST para:

`POST /api/auth/login-mock`

Exemplo de body:

```json
{
  "userId": "123",
  "email": "user@example.com",
  "roles": ["USER"]
}
```

### 3) Consumir endpoints protegidos

Envie o token no header:

`Authorization: Bearer <seu_token>`

Você pode usar `src/main/resources/http/test.http` para testar rapidamente.

## Documentacao Swagger (OpenAPI)

Com a aplicacao em execucao, acesse:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Fluxo recomendado no Swagger:

1. Chame `POST /api/auth/login-mock` para gerar um token.
2. Clique em **Authorize** no Swagger UI.
3. Informe: `Bearer <token>`.
4. Execute os endpoints protegidos (`/api/test/me`, `/api/test/user`, `/api/test/admin`).

Assim, voce consegue testar toda a autenticacao/autorizacao sem precisar configurar cliente externo.

## Como implementar no controller

Depois de configurado, **basta usar `@UsuarioAtual`** no parâmetro do método para receber o usuário autenticado já extraído do token JWT.

```java
@GetMapping("/me")
public UsuarioLogado me(@UsuarioAtual UsuarioLogado usuario) {
    return usuario;
}
```

Isso funciona porque o fluxo é:

1. `JwtAuthenticationFilter` valida o token e monta `UsuarioLogado`.
2. O usuário vai para o `SecurityContext`.
3. `UsuarioLogadoResolver` detecta `@UsuarioAtual` e injeta no controller.

Para restrição por papel:

```java
@GetMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public String adminOnly() {
    return "Admin access granted";
}
```

## Como testar

### Testes rápidos (manual)

1. Execute `auth.http` para gerar token (`USER` ou `ADMIN`).
2. Execute `test.http` para validar:
   - `/api/test/me`
   - `/api/test/user`
   - `/api/test/admin`

### Testes automatizados (Gradle)

```bash
./gradlew test
```

## Como implementar em projetos em andamento

Se você já tem um projeto Spring Boot e quer adicionar este módulo:

1. Copie o diretório `src/main/security-core` para o seu projeto (ou transforme em módulo compartilhado interno).
2. Garanta que o build inclua essa source folder (como em `build.gradle` deste projeto):

```groovy
sourceSets {
    main {
        java {
            srcDirs = ['src/main/java', 'src/main/security-core']
        }
    }
}
```

3. Mantenha `@SpringBootApplication(scanBasePackages = {"seu.pacote", "core", "auth"})` para o Spring encontrar os componentes de segurança.
4. Configure `security.jwt.secret` e `security.jwt.expiration` no ambiente.
5. Ajuste suas regras de autorização com `@PreAuthorize` conforme os papéis do seu domínio.
6. Nos controllers, use `@UsuarioAtual UsuarioLogado usuario` para acessar o usuário logado sem código extra de parsing JWT.

## Resumo prático

- O token chega no request.
- O filtro JWT valida e decodifica.
- O usuário autenticado é colocado no contexto do Spring Security.
- Você recebe esse usuário direto no controller com `@UsuarioAtual`.

Com isso, a implementação fica limpa, sem duplicar lógica de autenticação em cada endpoint.
