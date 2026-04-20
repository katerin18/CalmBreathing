package mpi.calmbreath.demo.repository;

import mpi.calmbreath.demo.model.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с refresh токенами
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    
    /**
     * Найти токен по значению
     */
    Optional<RefreshToken> findByToken(String token);
    
    /**
     * Удалить все токены пользователя
     */
    void deleteByUserId(String userId);
}
