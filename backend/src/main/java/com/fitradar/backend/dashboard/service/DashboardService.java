package com.fitradar.backend.dashboard.service;

import com.fitradar.backend.dashboard.dto.DashboardResponse;
import com.fitradar.backend.injury.model.InjuryRecord;
import com.fitradar.backend.injury.repository.InjuryRecordRepository;
import com.fitradar.backend.risk.dto.RiskPredictionResponse;
import com.fitradar.backend.risk.model.RiskPrediction;
import com.fitradar.backend.risk.repository.RiskPredictionRepository;
import com.fitradar.backend.shared.exception.ResourceNotFoundException;
import com.fitradar.backend.training.dto.TrainingSessionResponse;
import com.fitradar.backend.training.model.TrainingSession;
import com.fitradar.backend.training.repository.TrainingSessionRepository;
import com.fitradar.backend.user.model.User;
import com.fitradar.backend.user.repository.UserRepository;
import com.fitradar.backend.wellness.dto.WellnessResponse;
import com.fitradar.backend.wellness.model.Wellness;
import com.fitradar.backend.wellness.repository.WellnessRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final WellnessRepository wellnessRepository;
    private final TrainingSessionRepository trainingSessionRepository;
    private final RiskPredictionRepository riskPredictionRepository;
    private final InjuryRecordRepository injuryRecordRepository;

    public DashboardService(UserRepository userRepository,
                            WellnessRepository wellnessRepository,
                            TrainingSessionRepository trainingSessionRepository,
                            RiskPredictionRepository riskPredictionRepository,
                            InjuryRecordRepository injuryRecordRepository) {
        this.userRepository = userRepository;
        this.wellnessRepository = wellnessRepository;
        this.trainingSessionRepository = trainingSessionRepository;
        this.riskPredictionRepository = riskPredictionRepository;
        this.injuryRecordRepository = injuryRecordRepository;
    }

    public DashboardResponse getDashboard(String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Cargar datos
        List<Wellness> wellnessRecords = wellnessRepository
                .findByUserUsernameOrderByRecordDateDesc(username);
        List<TrainingSession> sessions = trainingSessionRepository
                .findByUserUsernameOrderBySessionDateDesc(username);
        List<RiskPrediction> predictions = riskPredictionRepository
                .findByUserUsernameOrderByPredictionDateDesc(username);
        List<InjuryRecord> injuries = injuryRecordRepository
                .findByUserUsernameOrderByStartDateDesc(username);

        // Sesiones de la semana actual
        LocalDate startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate endOfWeek   = startOfWeek.plusDays(6);

        List<TrainingSession> weekSessions = sessions.stream()
                .filter(s -> s.getSessionDate() != null)
                .filter(s -> !s.getSessionDate().isBefore(startOfWeek))
                .filter(s -> !s.getSessionDate().isAfter(endOfWeek))
                .collect(Collectors.toList());

        int weeklyMinutes = weekSessions.stream()
                .map(TrainingSession::getDurationMinutes)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        int weeklyCalories = weekSessions.stream()
                .map(TrainingSession::getCalories)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        int activeInjuries    = (int) injuries.stream().filter(i -> !i.isFullyRecovered()).count();
        int recoveredInjuries = (int) injuries.stream().filter(InjuryRecord::isFullyRecovered).count();

        // ACWR de la última sesión
        Double latestAcwr = sessions.isEmpty() ? null : sessions.get(0).getAcwr();

        // Progreso de personalización del modelo ML
        Integer samplesCollected = predictions.isEmpty() ? 0
                : predictions.get(0).getSamplesCollected();

        String fullName = (safe(user.getFirstName()) + " " + safe(user.getLastName())).trim();
        if (fullName.isBlank()) fullName = user.getUsername();

        DashboardResponse response = new DashboardResponse();
        response.setUsername(user.getUsername());
        response.setFullName(fullName);
        response.setLatestWellness(wellnessRecords.isEmpty() ? null
                : mapWellness(wellnessRecords.get(0)));
        response.setLatestTrainingSession(sessions.isEmpty() ? null
                : mapTrainingSession(sessions.get(0)));
        response.setLatestRiskPrediction(predictions.isEmpty() ? null
                : mapRiskPrediction(predictions.get(0)));
        response.setWeeklyTrainingSessions(weekSessions.size());
        response.setWeeklyTrainingMinutes(weeklyMinutes);
        response.setWeeklyCalories(weeklyCalories);
        response.setLatestAcwr(latestAcwr);
        response.setSamplesCollected(samplesCollected);
        response.setActiveInjuries(activeInjuries);
        response.setRecoveredInjuries(recoveredInjuries);

        return response;
    }

    // ── Mappers privados ──────────────────────────────────────────────────────

    private WellnessResponse mapWellness(Wellness w) {
        WellnessResponse r = new WellnessResponse();
        r.setId(w.getId());
        r.setUsername(w.getUser().getUsername());
        r.setRecordDate(w.getRecordDate());
        r.setSleepHours(w.getSleepHours());
        r.setSleepQuality(w.getSleepQuality());
        r.setFatigue(w.getFatigue());
        r.setStress(w.getStress());
        r.setMuscleSoreness(w.getMuscleSoreness());
        r.setGeneralFeeling(w.getGeneralFeeling());
        r.setRecoveryFeeling(w.getRecoveryFeeling());
        r.setHrvRmssd(w.getHrvRmssd());
        r.setRestingHr(w.getRestingHr());
        return r;
    }

    private TrainingSessionResponse mapTrainingSession(TrainingSession s) {
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
        r.setSessionLoad(s.getSessionLoad());
        r.setAcuteLoad7d(s.getAcuteLoad7d());
        r.setChronicLoad28d(s.getChronicLoad28d());
        r.setAcwr(s.getAcwr());
        r.setDaysSinceLastRest(s.getDaysSinceLastRest());
        r.setNotes(s.getNotes());
        return r;
    }

    private RiskPredictionResponse mapRiskPrediction(RiskPrediction p) {
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

    private String safe(String value) {
        return value == null ? "" : value;
    }
}