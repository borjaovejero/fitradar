package com.fitradar.backend.risk.controller;

import com.fitradar.backend.risk.dto.RiskPredictionRequest;
import com.fitradar.backend.risk.dto.RiskPredictionResponse;
import com.fitradar.backend.risk.model.RiskPrediction;
import com.fitradar.backend.risk.service.RiskPredictionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/risk-predictions")
@CrossOrigin
public class RiskPredictionController {

    private final RiskPredictionService riskPredictionService;

    public RiskPredictionController(RiskPredictionService riskPredictionService) {
        this.riskPredictionService = riskPredictionService;
    }

    // GET /api/risk-predictions
    @GetMapping
    public ResponseEntity<List<RiskPredictionResponse>> getMyPredictions(
            Authentication authentication) {

        List<RiskPredictionResponse> responses = riskPredictionService
                .getByUsername(authentication.getName())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // GET /api/risk-predictions/latest
    @GetMapping("/latest")
    public ResponseEntity<RiskPredictionResponse> getLatest(Authentication authentication) {
        RiskPrediction prediction = riskPredictionService.getLatest(authentication.getName());
        return ResponseEntity.ok(mapToResponse(prediction));
    }

    // GET /api/risk-predictions/{id}
    @GetMapping("/{id}")
    public ResponseEntity<RiskPredictionResponse> getById(
            @PathVariable Long id,
            Authentication authentication) {

        RiskPrediction prediction = riskPredictionService.getById(id);
        if (!prediction.getUser().getUsername().equals(authentication.getName())) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(mapToResponse(prediction));
    }

    // POST /api/risk-predictions/{id}/feedback
    // El usuario confirma si hubo lesión tras esa predicción → alimenta el modelo ML
    @PostMapping("/{id}/feedback")
    public ResponseEntity<Void> sendFeedback(
            @PathVariable Long id,
            @Valid @RequestBody RiskPredictionRequest request,
            Authentication authentication) {

        riskPredictionService.sendFeedback(authentication.getName(), id, request.getOutcome());
        return ResponseEntity.ok().build();
    }

    private RiskPredictionResponse mapToResponse(RiskPrediction p) {
        RiskPredictionResponse r = new RiskPredictionResponse();
        r.setId(p.getId());
        r.setUsername(p.getUser().getUsername());
        r.setTrainingSessionId(p.getTrainingSessionId());
        r.setPredictionDate(p.getPredictionDate());
        r.setRiskScore(p.getRiskScore());
        r.setGlobalProbability(p.getGlobalProbability());
        r.setRiskLevel(p.getRiskLevel());
        r.setModelUsed(p.getModelUsed());
        r.setSamplesCollected(p.getSamplesCollected());
        r.setRecommendation(p.getRecommendation());
        r.setModelVersion(p.getModelVersion());
        return r;
    }
}