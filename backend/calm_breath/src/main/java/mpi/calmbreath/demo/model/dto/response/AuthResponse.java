package mpi.calmbreath.demo.model.dto.response;

import lombok.*;

/**
 * DTO для ответа с JWT токенами
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    
    private String accessToken;
    private String refreshToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private Long expiresIn; // в миллисекундах
    private UserResponse user;
}
