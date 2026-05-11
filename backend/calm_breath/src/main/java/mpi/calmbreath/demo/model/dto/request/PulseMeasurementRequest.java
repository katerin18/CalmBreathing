package mpi.calmbreath.demo.model.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO для передачи данных о пульсе с фронтенда
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PulseMeasurementRequest {

    @NotNull(message = "Начальный пульс не может быть пустым")
    @Positive(message = "Начальный пульс должен быть положительным")
    private Integer startPulse;

    @NotNull(message = "Длительность упражнения не может быть пустой")
    @Positive(message = "Длительность упражнения должна быть положительной")
    private Integer exerciseDurationSeconds;

    @NotNull(message = "Конечный пульс не может быть пустым")
    @Positive(message = "Конечный пульс должен быть положительным")
    private Integer endPulse;

    private LocalDateTime measuredAt;
}