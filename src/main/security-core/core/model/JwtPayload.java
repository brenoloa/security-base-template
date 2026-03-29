package core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtPayload {
    private String subject;
    private String email;
    private List<String> roles;
    private String userId;
    private Map<String, Object> otherClaims;
}
