package mpi.calmbreath.demo.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO для обновления токена доступа
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshTokenRequest {
    
    @NotBlank(message = "Refresh token не может быть пустым")
    private String refreshToken;
}
