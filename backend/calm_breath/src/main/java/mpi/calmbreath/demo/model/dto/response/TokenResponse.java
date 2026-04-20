package mpi.calmbreath.demo.model.dto.response;

import lombok.*;

/**
 * DTO для ответа с новым токеном доступа
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenResponse {
    
    private String accessToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
}
