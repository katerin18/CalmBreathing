package mpi.calmbreath.demo.controller;

import lombok.extern.slf4j.Slf4j;
import mpi.calmbreath.demo.model.dto.response.UserResponse;
import mpi.calmbreath.demo.service.UserService;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * REST контроллер для профиля пользователя
 */
@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * Получить текущего пользователя
     * GET /api/user/me
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated()) {
            String email = auth.getName();
            log.info("Getting current user profile: {}", email);
            
            var user = userService.getUserByEmail(email);
            UserResponse response = userService.mapUserToResponse(user);
            
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.status(401).build();
    }
    
    /**
     * Получить пользователя по ID
     * GET /api/user/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable UUID userId) {
        
        log.info("Getting user profile: {}", userId);
        
        var user = userService.getUserById(userId);
        UserResponse response = userService.mapUserToResponse(user);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Обновить профиль пользователя
     * PUT /api/user/me
     */
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated()) {
            String email = auth.getName();
            var user = userService.getUserByEmail(email);
            
            log.info("Updating user profile: {}", user.getId());
            
            UserResponse response = userService.updateUserProfile(
                    user.getId(),
                    firstName,
                    lastName
            );
            
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.status(401).build();
    }
    
    /**
     * Изменить пароль пользователя
     * PUT /api/user/password
     */
    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated()) {
            String email = auth.getName();
            var user = userService.getUserByEmail(email);
            
            log.info("Changing password for user: {}", user.getId());
            
            userService.changePassword(
                    user.getId(),
                    oldPassword,
                    newPassword
            );
            
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.status(401).build();
    }
}
