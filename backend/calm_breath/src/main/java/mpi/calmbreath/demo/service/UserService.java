package mpi.calmbreath.demo.service;

import lombok.extern.slf4j.Slf4j;
import mpi.calmbreath.demo.error.CustomException;
import mpi.calmbreath.demo.model.dto.response.UserResponse;
import mpi.calmbreath.demo.model.entity.User;
import mpi.calmbreath.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис для работы с пользователями
 */
@Service
@Slf4j
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    /**
     * Получить пользователя по ID
     */
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(
                        "Пользователь не найден",
                        "USER_NOT_FOUND",
                        HttpStatus.NOT_FOUND.value()
                ));
    }
    
    /**
     * Получить пользователя по email
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(
                        "Пользователь не найден",
                        "USER_NOT_FOUND",
                        HttpStatus.NOT_FOUND.value()
                ));
    }
    
    /**
     * Обновить профиль пользователя
     */
    public UserResponse updateUserProfile(UUID userId, String firstName, String lastName) {
        log.info("Updating user profile: {}", userId);
        
        User user = getUserById(userId);
        
        if (firstName != null && !firstName.isBlank()) {
            user.setFirstName(firstName);
        }
        
        if (lastName != null && !lastName.isBlank()) {
            user.setLastName(lastName);
        }
        
        User updatedUser = userRepository.save(user);
        
        log.info("User profile updated: {}", userId);
        
        return mapUserToResponse(updatedUser);
    }
    
    /**
     * Изменить пароль пользователя
     */
    public void changePassword(UUID userId, String oldPassword, String newPassword) {
        log.info("Changing password for user: {}", userId);
        
        User user = getUserById(userId);
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new CustomException(
                    "Неправильный текущий пароль",
                    "INVALID_OLD_PASSWORD",
                    HttpStatus.UNAUTHORIZED.value()
            );
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        log.info("Password changed for user: {}", userId);
    }
    
    /**
     * Конвертировать User в UserResponse
     */
    public UserResponse mapUserToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .enabled(user.getEnabled())
                .emailVerified(user.getEmailVerified())
                .roles(user.getRoles()
                        .stream()
                        .map(role -> role.getName().toString())
                        .collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt().format(DATE_FORMATTER))
                .updatedAt(user.getUpdatedAt().format(DATE_FORMATTER))
                .build();
    }
}
