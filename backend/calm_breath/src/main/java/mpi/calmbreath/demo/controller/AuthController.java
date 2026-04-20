package mpi.calmbreath.demo.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import mpi.calmbreath.demo.model.dto.request.LoginRequest;
import mpi.calmbreath.demo.model.dto.request.RefreshTokenRequest;
import mpi.calmbreath.demo.model.dto.request.RegisterRequest;
import mpi.calmbreath.demo.model.dto.response.AuthResponse;
import mpi.calmbreath.demo.model.dto.response.TokenResponse;
import mpi.calmbreath.demo.service.AuthService;
import mpi.calmbreath.demo.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * REST контроллер для аутентификации
 */
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private JwtProvider jwtProvider;
    
    /**
     * Регистрация нового пользователя
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        
        log.info("Register request received for email: {}", request.getEmail());
        
        AuthResponse response = authService.register(request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    
    /**
     * Вход пользователя
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        
        log.info("Login request received for email: {}", request.getEmail());
        
        AuthResponse response = authService.login(request);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Обновление access token
     * POST /api/auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        
        log.info("Refresh token request received");
        
        TokenResponse response = authService.refreshAccessToken(request);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Выход пользователя
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated()) {
            String token = getTokenFromRequest();
            if (token != null) {
                String userId = jwtProvider.getUserIdFromToken(token);
                authService.logout(userId);
                log.info("User logout successful");
            }
        }
        
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Получить токен из заголовка Authorization
     */
    private String getTokenFromRequest() {
        // Метод будет вызван из контекста запроса в фильтре
        // Здесь просто вспомогательный метод
        return null;
    }
}
