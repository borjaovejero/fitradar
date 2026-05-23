package com.fitradar.backend.wellness.controller;

import com.fitradar.backend.wellness.dto.WellnessRequest;
import com.fitradar.backend.wellness.dto.WellnessResponse;
import com.fitradar.backend.wellness.model.Wellness;
import com.fitradar.backend.wellness.service.WellnessService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wellness")
@CrossOrigin
public class WellnessController {

    private final WellnessService wellnessService;

    public WellnessController(WellnessService wellnessService) {
        this.wellnessService = wellnessService;
    }

    // POST /api/wellness
    @PostMapping
    public ResponseEntity<WellnessResponse> createRecord(
            @Valid @RequestBody WellnessRequest request,
            Authentication authentication) {

        String username = authentication.getName();
        Wellness record = wellnessService.createRecord(username, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(record));
    }

    // PUT /api/wellness/{id}
    @PutMapping("/{id}")
    public ResponseEntity<WellnessResponse> updateRecord(
            @PathVariable Long id,
            @Valid @RequestBody WellnessRequest request,
            Authentication authentication) {

        String username = authentication.getName();
        Wellness record = wellnessService.updateRecord(username, id, request);
        return ResponseEntity.ok(mapToResponse(record));
    }

    // GET /api/wellness
    @GetMapping
    public ResponseEntity<List<WellnessResponse>> getMyRecords(Authentication authentication) {
        String username = authentication.getName();
        List<WellnessResponse> responses = wellnessService.getRecordsByUsername(username)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    private WellnessResponse mapToResponse(Wellness record) {
        WellnessResponse r = new WellnessResponse();
        r.setId(record.getId());
        r.setUsername(record.getUser().getUsername());
        r.setRecordDate(record.getRecordDate());
        r.setSleepHours(record.getSleepHours());
        r.setSleepQuality(record.getSleepQuality());
        r.setFatigue(record.getFatigue());
        r.setStress(record.getStress());
        r.setMuscleSoreness(record.getMuscleSoreness());
        r.setGeneralFeeling(record.getGeneralFeeling());
        r.setRecoveryFeeling(record.getRecoveryFeeling());
        r.setHrvRmssd(record.getHrvRmssd());
        r.setRestingHr(record.getRestingHr());
        return r;
    }
}