package mpi.calmbreath.demo.model.dto.response;

import lombok.*;
import java.util.Set;
import java.util.UUID;

/**
 * DTO для ответа профиля пользователя
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private Boolean enabled;
    private Boolean emailVerified;
    private Set<String> roles;
    private String createdAt;
    private String updatedAt;
}
