package mpi.calmbreath.demo.repository;

import mpi.calmbreath.demo.model.entity.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с замерами пульса
 */
@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, UUID> {

    Optional<Measurement> findTopByUser_IdOrderByMeasuredAtDescCreatedAtDesc(UUID userId);
    List<Measurement> findByUser_IdOrderByMeasuredAtDescCreatedAtDesc(UUID userId);

}