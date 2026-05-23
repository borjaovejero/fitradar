package com.fitradar.backend.training.controller;

import com.fitradar.backend.training.dto.TrainingSessionRequest;
import com.fitradar.backend.training.dto.TrainingSessionResponse;
import com.fitradar.backend.training.model.TrainingSession;
import com.fitradar.backend.training.service.TrainingSessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/training-sessions")
@CrossOrigin
public class TrainingSessionController {

    private final TrainingSessionService trainingSessionService;

    public TrainingSessionController(TrainingSessionService trainingSessionService) {
        this.trainingSessionService = trainingSessionService;
    }

    // POST /api/training-sessions
    @PostMapping
    public ResponseEntity<TrainingSessionResponse> createSession(
            @Valid @RequestBody TrainingSessionRequest request,
            Authentication authentication) {

        String username = authentication.getName();
        System.out.println(">>> durationMinutes recibido: " + request.getDurationMinutes());
        System.out.println(">>> title recibido: " + request.getTitle());
        System.out.println(">>> trainingType recibido: " + request.getTrainingType());
        TrainingSession session = trainingSessionService.createSession(username, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(session));
    }

    // PUT /api/training-sessions/{id}
    @PutMapping("/{id}")
    public ResponseEntity<TrainingSessionResponse> updateSession(
            @PathVariable Long id,
            @Valid @RequestBody TrainingSessionRequest request,
            Authentication authentication) {

        String username = authentication.getName();
        TrainingSession session = trainingSessionService.updateSession(username, id, request);
        return ResponseEntity.ok(mapToResponse(session));
    }

    // GET /api/training-sessions
    @GetMapping
    public ResponseEntity<List<TrainingSessionResponse>> getMySessions(Authentication authentication) {
        String username = authentication.getName();
        List<TrainingSessionResponse> responses = trainingSessionService
                .getSessionsByUsername(username)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // GET /api/training-sessions/between?startDate=&endDate=
    @GetMapping("/between")
    public ResponseEntity<List<TrainingSessionResponse>> getSessionsBetween(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            Authentication authentication) {

        String username = authentication.getName();
        List<TrainingSessionResponse> responses = trainingSessionService
                .getSessionsBetweenDates(username, startDate, endDate)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // GET /api/training-sessions/{id}
    @GetMapping("/{id}")
    public ResponseEntity<TrainingSessionResponse> getSessionById(
            @PathVariable Long id,
            Authentication authentication) {

        TrainingSession session = trainingSessionService.getSessionById(id);
        if (!session.getUser().getUsername().equals(authentication.getName()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(mapToResponse(session));
    }

    // DELETE /api/training-sessions/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(
            @PathVariable Long id,
            Authentication authentication) {

        trainingSessionService.deleteSession(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }

    private TrainingSessionResponse mapToResponse(TrainingSession s) {
        TrainingSessionResponse r = new TrainingSessionResponse();
        r.setId(s.getId());
        r.setUsername(s.getUser().getUsername());
        r.setSessionDate(s.getSessionDate());
        r.setTitle(s.getTitle());
        r.setTrainingType(s.getTrainingType());
        r.setDurationMinutes(s.getDurationMinutes());
        r.setRpe(s.getRpe());
        r.setIntensityLevel(s.getIntensityLevel());
        r.setDistanceKm(s.getDistanceKm());
        r.setAverageHeartRate(s.getAverageHeartRate());
        r.setMaxHeartRate(s.getMaxHeartRate());
        r.setCalories(s.getCalories());
        r.setNotes(s.getNotes());
        r.setSessionLoad(s.getSessionLoad());
        r.setAcuteLoad7d(s.getAcuteLoad7d());
        r.setChronicLoad28d(s.getChronicLoad28d());
        r.setAcwr(s.getAcwr());
        r.setDaysSinceLastRest(s.getDaysSinceLastRest());
        return r;
    }
}