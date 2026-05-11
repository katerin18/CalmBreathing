package mpi.calmbreath.demo.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO для ответа с данными о пульсе
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PulseMeasurementResponse {

    private UUID id;
    private UUID userId;
    private Integer startPulse;
    private Integer exerciseDurationSeconds;
    private Integer endPulse;
    private LocalDateTime measuredAt;
    private LocalDateTime createdAt;
}