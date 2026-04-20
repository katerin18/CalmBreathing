package mpi.calmbreath.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;
import mpi.calmbreath.demo.model.enums.UserRole;
import java.util.UUID;

/**
 * Сущность роли пользователя
 */
@Entity
@Table(name = "roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "name", unique = true, nullable = false)
    private UserRole name;
    
    @Column(name = "description")
    private String description;
}
