package com.fitradar.backend.risk.service;

import com.fitradar.backend.injury.model.InjuryRecord;
import com.fitradar.backend.injury.repository.InjuryRecordRepository;
import com.fitradar.backend.risk.model.RiskPrediction;
import com.fitradar.backend.risk.repository.RiskPredictionRepository;
import com.fitradar.backend.shared.exception.ResourceNotFoundException;
import com.fitradar.backend.training.model.TrainingSession;
import com.fitradar.backend.user.model.User;
import com.fitradar.backend.user.repository.UserRepository;
import com.fitradar.backend.wellness.model.Wellness;
import com.fitradar.backend.wellness.repository.WellnessRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;

@Service
public class RiskPredictionService {

    private static final Logger log = LoggerFactory.getLogger(RiskPredictionService.class);

    private final RiskPredictionRepository riskPredictionRepository;
    private final UserRepository userRepository;
    private final WellnessRepository wellnessRepository;
    private final InjuryRecordRepository injuryRecordRepository;
    private final RestTemplate restTemplate;

    @Value("${ml.api.url}")
    private String mlApiUrl;

    public RiskPredictionService(RiskPredictionRepository riskPredictionRepository,
                                 UserRepository userRepository,
                                 WellnessRepository wellnessRepository,
                                 InjuryRecordRepository injuryRecordRepository,
                                 RestTemplate restTemplate) {
        this.riskPredictionRepository   = riskPredictionRepository;
        this.userRepository             = userRepository;
        this.wellnessRepository         = wellnessRepository;
        this.injuryRecordRepository     = injuryRecordRepository;
        this.restTemplate               = restTemplate;
    }

    // ── Predicción automática ─────────────────────────────────────────────────

    public Optional<RiskPrediction> predictForSession(User user, TrainingSession session) {
        try {
            Map<String, Object> payload = buildMlPayload(user, session);

            @SuppressWarnings("unchecked")
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    mlApiUrl + "/predict?user_id=" + user.getUsername(),
                    payload,
                    Map.class
            );

            Map<String, Object> mlResponse = response.getBody();
            if (mlResponse == null) return Optional.empty();

            RiskPrediction prediction = new RiskPrediction();
            prediction.setUser(user);
            prediction.setTrainingSessionId(session.getId());
            prediction.setPredictionDate(LocalDateTime.now());
            prediction.setRiskScore(toDouble(mlResponse.get("risk_probability")));
            prediction.setGlobalProbability(toDouble(mlResponse.get("global_probability")));
            prediction.setRiskLevel(parseRiskLevel(mlResponse.get("risk_label")));
            prediction.setModelUsed((String) mlResponse.get("model_used"));
            prediction.setSamplesCollected(toInt(mlResponse.get("samples_collected")));
            prediction.setRecommendation(buildRecommendation(prediction.getRiskLevel()));
            prediction.setModelVersion("v4");

            return Optional.of(riskPredictionRepository.save(prediction));

        } catch (Exception e) {
            log.error("Error llamando a la API ML para usuario {}: {}",
                    user.getUsername(), e.getMessage());
            return Optional.empty();
        }
    }

    // ── Feedback del usuario ──────────────────────────────────────────────────

    public void sendFeedback(String username, Long predictionId, int outcome) {
        RiskPrediction prediction = riskPredictionRepository.findById(predictionId)
                .orElseThrow(() -> new ResourceNotFoundException("Predicción no encontrada"));

        if (!prediction.getUser().getUsername().equals(username)) {
            throw new ResourceNotFoundException("Predicción no encontrada");
        }

        try {
            Map<String, Object> feedbackPayload = new HashMap<>();
            feedbackPayload.put("outcome", outcome);
            feedbackPayload.put("user_id", username);

            restTemplate.postForEntity(
                    mlApiUrl + "/update/" + predictionId,
                    feedbackPayload,
                    Map.class
            );

            log.info("Feedback enviado al modelo ML — predicción {} outcome={}",
                    predictionId, outcome);

        } catch (Exception e) {
            log.error("Error enviando feedback al modelo ML: {}", e.getMessage());
        }
    }

    // ── Consultas ─────────────────────────────────────────────────────────────

    public List<RiskPrediction> getByUsername(String username) {
        if (!userRepository.existsById(username))
            throw new ResourceNotFoundException("Usuario no encontrado");
        return riskPredictionRepository.findByUserUsernameOrderByPredictionDateDesc(username);
    }

    public RiskPrediction getLatest(String username) {
        return riskPredictionRepository
                .findTopByUserUsernameOrderByPredictionDateDesc(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No hay predicciones para este usuario"));
    }

    public RiskPrediction getById(Long id) {
        return riskPredictionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Predicción no encontrada"));
    }

    // ── Construcción del payload ML ───────────────────────────────────────────

    private Map<String, Object> buildMlPayload(User user, TrainingSession session) {
        Map<String, Object> payload = new HashMap<>();

        // ── Datos del usuario ─────────────────────────────────────────────────
        if (user.getBirthDate() != null) {
            payload.put("age", LocalDate.now().getYear() - user.getBirthDate().getYear());
        }
        if (user.getHeightCm() != null && user.getWeightKg() != null) {
            double heightM = user.getHeightCm() / 100.0;
            payload.put("bmi", user.getWeightKg() / (heightM * heightM));
        }

        // ── Historial de lesiones (del registro real, no del perfil) ──────────
        List<InjuryRecord> injuries = injuryRecordRepository
                .findByUserUsernameOrderByStartDateDesc(user.getUsername());

        int totalInjuries  = injuries.size();
        long activeInjuries = injuries.stream()
                .filter(i -> !i.isFullyRecovered()).count();

        // Días desde la última lesión (999 si nunca ha tenido)
        OptionalLong daysSinceLastInjury = injuries.stream()
                .filter(i -> i.getStartDate() != null)
                .mapToLong(i -> ChronoUnit.DAYS.between(i.getStartDate(), LocalDate.now()))
                .min();

        // previous_injury: 1 si tiene alguna lesión registrada, 0 si no
        payload.put("previous_injury",        totalInjuries > 0 ? 1 : 0);
        payload.put("active_injuries",        (int) activeInjuries);
        payload.put("total_injuries",         totalInjuries);
        payload.put("days_since_last_injury", daysSinceLastInjury.isPresent()
                ? (int) daysSinceLastInjury.getAsLong() : 999);

        // ── Datos del entrenamiento ───────────────────────────────────────────
        payload.put("training_duration",  session.getDurationMinutes());
        payload.put("training_intensity", session.getRpe());
        payload.put("training_load",      session.getSessionLoad());
        payload.put("heart_rate",         session.getAverageHeartRate());
        payload.put("biomechanical_load", session.getAcwr());
        payload.put("acwr",               session.getAcwr());
        payload.put("rest_period",        session.getDaysSinceLastRest());

        // ── Wellness del mismo día ────────────────────────────────────────────
        wellnessRepository
                .findByUserUsernameAndRecordDate(
                        user.getUsername(),
                        session.getSessionDate())
                .ifPresent(w -> {
                    payload.put("sleep",    w.getSleepHours() != null
                            ? w.getSleepHours() * 60 : null);
                    payload.put("recovery", w.getHrvRmssd());
                    payload.put("fatigue",  buildFatigueScore(w));
                });

        payload.put("source_id", -1);
        return payload;
    }

    private double buildFatigueScore(Wellness w) {
        int fatigue  = w.getFatigue()        != null ? w.getFatigue()        : 0;
        int soreness = w.getMuscleSoreness() != null ? w.getMuscleSoreness() : 0;
        int stress   = w.getStress()         != null ? w.getStress()         : 0;
        return fatigue + soreness + stress;
    }

    private RiskPrediction.RiskLevel parseRiskLevel(Object label) {
        if (label == null) return RiskPrediction.RiskLevel.LOW;
        return switch (label.toString()) {
            case "Riesgo alto"  -> RiskPrediction.RiskLevel.HIGH;
            case "Riesgo medio" -> RiskPrediction.RiskLevel.MEDIUM;
            default             -> RiskPrediction.RiskLevel.LOW;
        };
    }

    private String buildRecommendation(RiskPrediction.RiskLevel level) {
        return switch (level) {
            case HIGH   -> "Riesgo elevado detectado. Considera descansar o reducir la intensidad.";
            case MEDIUM -> "Riesgo moderado. Presta atención a la recuperación y el sueño.";
            case LOW    -> "Riesgo bajo. Puedes entrenar con normalidad.";
        };
    }

    private Double toDouble(Object val) {
        if (val == null) return null;
        if (val instanceof Number) return ((Number) val).doubleValue();
        return Double.parseDouble(val.toString());
    }

    private Integer toInt(Object val) {
        if (val == null) return null;
        if (val instanceof Number) return ((Number) val).intValue();
        return Integer.parseInt(val.toString());
    }
}