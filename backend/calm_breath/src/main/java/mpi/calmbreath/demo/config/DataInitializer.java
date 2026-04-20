package mpi.calmbreath.demo.config;

import lombok.extern.slf4j.Slf4j;
import mpi.calmbreath.demo.model.entity.Role;
import mpi.calmbreath.demo.model.enums.UserRole;
import mpi.calmbreath.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Инициализация данных при старте приложения
 */
@Configuration
@Slf4j
public class DataInitializer {
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Bean
    public CommandLineRunner initRoles() {
        return args -> {
            log.info("Initializing application roles...");
            
            // Инициализация ролей
            if (!roleRepository.findByName(UserRole.ROLE_USER).isPresent()) {
                Role userRole = Role.builder()
                        .name(UserRole.ROLE_USER)
                        .description("Обычный пользователь")
                        .build();
                roleRepository.save(userRole);
                log.info("ROLE_USER created");
            }
            
            if (!roleRepository.findByName(UserRole.ROLE_ADMIN).isPresent()) {
                Role adminRole = Role.builder()
                        .name(UserRole.ROLE_ADMIN)
                        .description("Администратор")
                        .build();
                roleRepository.save(adminRole);
                log.info("ROLE_ADMIN created");
            }
            
            if (!roleRepository.findByName(UserRole.ROLE_MODERATOR).isPresent()) {
                Role moderatorRole = Role.builder()
                        .name(UserRole.ROLE_MODERATOR)
                        .description("Модератор")
                        .build();
                roleRepository.save(moderatorRole);
                log.info("ROLE_MODERATOR created");
            }
            
            log.info("Roles initialization completed");
        };
    }
}
