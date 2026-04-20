package mpi.calmbreath.demo.repository;

import mpi.calmbreath.demo.model.entity.Role;
import mpi.calmbreath.demo.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с ролями
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    
    /**
     * Найти роль по названию
     */
    Optional<Role> findByName(UserRole name);
}
