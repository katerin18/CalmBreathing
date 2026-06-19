package mpi.calmbreath.demo.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import mpi.calmbreath.demo.model.dto.request.PulseMeasurementRequest;
import mpi.calmbreath.demo.model.dto.response.PulseMeasurementResponse;
import mpi.calmbreath.demo.service.MeasurementService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * REST контроллер для замеров пульса
 */
@RestController
@RequestMapping("/api/measurements")
@Slf4j
public class MeasurementController {

    @Autowired
    private MeasurementService measurementService;

    /**
     * Сохранить новый замер
     * POST /api/measurements
     */
    @PostMapping
    public ResponseEntity<PulseMeasurementResponse> createMeasurement(
            @Valid @RequestBody PulseMeasurementRequest request) {

        log.info("Creating pulse measurement");

        PulseMeasurementResponse response = measurementService.createMeasurement(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Получить последний замер текущего пользователя
     * GET /api/measurements/latest
     */
    @GetMapping("/latest")
    public ResponseEntity<PulseMeasurementResponse> getLatestMeasurement() {
        log.info("Getting latest pulse measurement");

        PulseMeasurementResponse response = measurementService.getLatestMeasurement();
        return ResponseEntity.ok(response);
    }

    /**
     * Получить всю историю измерений текущего пользователя
     * GET /api/measurements
     */
    @GetMapping
    public ResponseEntity<List<PulseMeasurementResponse>> getAllMeasurements() {
        log.info("Getting all pulse measurements for current user");

        List<PulseMeasurementResponse> response = measurementService.getAllMeasurements();
        return ResponseEntity.ok(response);
    }
}