package mpi.calmbreath.demo.service;

import lombok.extern.slf4j.Slf4j;
import mpi.calmbreath.demo.error.CustomException;
import mpi.calmbreath.demo.model.dto.request.LoginRequest;
import mpi.calmbreath.demo.model.dto.request.RefreshTokenRequest;
import mpi.calmbreath.demo.model.dto.request.RegisterRequest;
import mpi.calmbreath.demo.model.dto.response.AuthResponse;
import mpi.calmbreath.demo.model.dto.response.TokenResponse;
import mpi.calmbreath.demo.model.entity.RefreshToken;
import mpi.calmbreath.demo.model.entity.Role;
import mpi.calmbreath.demo.model.entity.User;
import mpi.calmbreath.demo.model.enums.UserRole;
import mpi.calmbreath.demo.repository.RefreshTokenRepository;
import mpi.calmbreath.demo.repository.RoleRepository;
import mpi.calmbreath.demo.repository.UserRepository;
import mpi.calmbreath.demo.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashSet;

/**
 * Сервис аутентификации
 */
@Service
@Slf4j
@Transactional
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtProvider jwtProvider;
    
    @Autowired
    private UserService userService;
    
    /**
     * Регистрация нового пользователя
     */
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());
        
        // Проверка существования пользователя
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("User already exists with email: {}", request.getEmail());
            throw new CustomException(
                    "Пользователь с таким email уже существует",
                    "USER_ALREADY_EXISTS",
                    HttpStatus.CONFLICT.value()
            );
        }
        
        // Проверка совпадения паролей
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new CustomException(
                    "Пароли не совпадают",
                    "PASSWORD_MISMATCH",
                    HttpStatus.BAD_REQUEST.value()
            );
        }
        
        // Создание пользователя
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .enabled(true)
                .emailVerified(false)
                .roles(new HashSet<>())
                .build();
        
        // Присвоение роли ROLE_USER
        Role userRole = roleRepository.findByName(UserRole.ROLE_USER)
                .orElseThrow(() -> new CustomException(
                        "Роль не найдена",
                        "ROLE_NOT_FOUND",
                        HttpStatus.INTERNAL_SERVER_ERROR.value()
                ));
        
        user.getRoles().add(userRole);
        User savedUser = userRepository.save(user);
        
        log.info("User registered successfully with id: {}", savedUser.getId());
        
        return generateAuthResponse(savedUser);
    }
    
    /**
     * Вход пользователя
     */
    public AuthResponse login(LoginRequest request) {
        log.info("User login attempt with email: {}", request.getEmail());
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", request.getEmail());
                    return new CustomException(
                            "Неправильный email или пароль",
                            "INVALID_CREDENTIALS",
                            HttpStatus.UNAUTHORIZED.value()
                    );
                });
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Invalid password for user: {}", request.getEmail());
            throw new CustomException(
                    "Неправильный email или пароль",
                    "INVALID_CREDENTIALS",
                    HttpStatus.UNAUTHORIZED.value()
            );
        }
        
        if (!user.getEnabled()) {
            throw new CustomException(
                    "Пользователь отключен",
                    "USER_DISABLED",
                    HttpStatus.FORBIDDEN.value()
            );
        }
        
        log.info("User login successful: {}", request.getEmail());
        
        return generateAuthResponse(user);
    }
    
    /**
     * Обновление access token
     */
    public TokenResponse refreshAccessToken(RefreshTokenRequest request) {
        log.info("Refreshing access token");
        
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new CustomException(
                        "Refresh token не найден",
                        "REFRESH_TOKEN_NOT_FOUND",
                        HttpStatus.UNAUTHORIZED.value()
                ));
        
        if (refreshToken.getRevoked() || refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new CustomException(
                    "Refresh token истек",
                    "REFRESH_TOKEN_EXPIRED",
                    HttpStatus.UNAUTHORIZED.value()
            );
        }
        
        User user = refreshToken.getUser();
        String accessToken = generateAccessToken(user);
        
        return TokenResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(jwtProvider.getAccessTokenExpiration())
                .build();
    }
    
    /**
     * Выход пользователя (удаление refresh token)
     */
    public void logout(String userId) {
        log.info("User logout: {}", userId);
        refreshTokenRepository.deleteByUserId(userId);
    }
    
    /**
     * Генерация пары токенов (access + refresh)
     */
    private AuthResponse generateAuthResponse(User user) {
        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);
        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtProvider.getAccessTokenExpiration())
                .user(userService.mapUserToResponse(user))
                .build();
    }
    
    /**
     * Генерация access token
     */
    private String generateAccessToken(User user) {
        var authorities = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().toString()))
                .toList();
        
        return jwtProvider.generateAccessToken(user.getEmail(), user.getId(), authorities);
    }
    
    /**
     * Генерация refresh token и сохранение в БД
     */
    private String generateRefreshToken(User user) {
        String token = jwtProvider.generateRefreshToken(user.getEmail(), user.getId());
        
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();
        
        refreshTokenRepository.save(refreshToken);
        
        return token;
    }
}
