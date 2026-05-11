package mpi.calmbreath.demo.service;

import lombok.extern.slf4j.Slf4j;
import mpi.calmbreath.demo.error.CustomException;
import mpi.calmbreath.demo.model.dto.request.PulseMeasurementRequest;
import mpi.calmbreath.demo.model.dto.response.PulseMeasurementResponse;
import mpi.calmbreath.demo.model.entity.Measurement;
import mpi.calmbreath.demo.model.entity.User;
import mpi.calmbreath.demo.repository.MeasurementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Сервис для работы с замерами пульса
 */
@Service
@Slf4j
@Transactional
public class MeasurementService {

    @Autowired
    private MeasurementRepository measurementRepository;

    @Autowired
    private UserService userService;

    public PulseMeasurementResponse createMeasurement(PulseMeasurementRequest request) {
        User user = getCurrentUser();
        PulseMeasurementRequest normalizedRequest = validateAndNormalizeRequest(request);

        Measurement measurement = Measurement.builder()
                .user(user)
                .startPulse(normalizedRequest.getStartPulse())
                .exerciseDurationSeconds(normalizedRequest.getExerciseDurationSeconds())
                .endPulse(normalizedRequest.getEndPulse())
                .measuredAt(normalizedRequest.getMeasuredAt())
                .build();

        Measurement savedMeasurement = measurementRepository.save(measurement);
        log.info("Saved pulse measurement for user: {}", user.getId());

        return mapToResponse(savedMeasurement);
    }

    public PulseMeasurementResponse getLatestMeasurement() {
        User user = getCurrentUser();

        Measurement measurement = measurementRepository
                .findTopByUser_IdOrderByMeasuredAtDescCreatedAtDesc(user.getId())
                .orElseThrow(() -> new CustomException(
                        "Замеры для пользователя не найдены",
                        "MEASUREMENT_NOT_FOUND",
                        HttpStatus.NOT_FOUND.value()
                ));

        return mapToResponse(measurement);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new CustomException(
                    "Пользователь не авторизован",
                    "UNAUTHORIZED",
                    HttpStatus.UNAUTHORIZED.value()
            );
        }

        return userService.getUserByEmail(authentication.getName());
    }

    private PulseMeasurementRequest validateAndNormalizeRequest(PulseMeasurementRequest request) {
        if (request == null) {
            throw new CustomException(
                    "Данные замера не переданы",
                    "INVALID_MEASUREMENT_DATA",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        if (request.getStartPulse() == null || request.getExerciseDurationSeconds() == null || request.getEndPulse() == null) {
            throw new CustomException(
                    "Обязательные поля замера должны быть заполнены",
                    "INVALID_MEASUREMENT_DATA",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        LocalDateTime measuredAt = request.getMeasuredAt() != null ? request.getMeasuredAt() : LocalDateTime.now();

        return PulseMeasurementRequest.builder()
                .startPulse(request.getStartPulse())
                .exerciseDurationSeconds(request.getExerciseDurationSeconds())
                .endPulse(request.getEndPulse())
                .measuredAt(measuredAt)
                .build();
    }

    private PulseMeasurementResponse mapToResponse(Measurement measurement) {
        return PulseMeasurementResponse.builder()
                .id(measurement.getId())
                .userId(measurement.getUser().getId())
                .startPulse(measurement.getStartPulse())
                .exerciseDurationSeconds(measurement.getExerciseDurationSeconds())
                .endPulse(measurement.getEndPulse())
                .measuredAt(measurement.getMeasuredAt())
                .createdAt(measurement.getCreatedAt())
                .build();
    }
}