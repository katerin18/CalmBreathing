package mpi.calmbreath.demo.repository;

import mpi.calmbreath.demo.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с пользователями
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    /**
     * Найти пользователя по email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Проверить существование пользователя по email
     */
    boolean existsByEmail(String email);
}
