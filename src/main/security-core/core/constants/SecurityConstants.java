package core.constants;

public final class SecurityConstants {
    private SecurityConstants() {}

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    
    // Default expiration: 1 hour (3600 seconds)
    public static final long DEFAULT_TOKEN_EXPIRATION = 3600;

    // Claims keys
    public static final String CLAIM_USER_ID = "userId";
    public static final String CLAIM_ROLES = "roles";
    public static final String CLAIM_EMAIL = "email";
}
