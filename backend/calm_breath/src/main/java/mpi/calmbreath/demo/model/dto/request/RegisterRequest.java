package mpi.calmbreath.demo.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO для регистрации нового пользователя
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
    
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Email должен быть корректным")
    private String email;
    
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 6, max = 50, message = "Пароль должен быть от 6 до 50 символов")
    private String password;
    
    @NotBlank(message = "Повторный пароль не может быть пустым")
    private String confirmPassword;
    
    @Size(max = 50, message = "Имя не должно превышать 50 символов")
    private String firstName;
    
    @Size(max = 50, message = "Фамилия не должна превышать 50 символов")
    private String lastName;
}
