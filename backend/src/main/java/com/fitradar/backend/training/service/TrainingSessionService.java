package com.fitradar.backend.training.service;

import com.fitradar.backend.risk.service.RiskPredictionService;
import com.fitradar.backend.shared.exception.BusinessException;
import com.fitradar.backend.shared.exception.ResourceNotFoundException;
import com.fitradar.backend.training.dto.TrainingSessionRequest;
import com.fitradar.backend.training.model.TrainingSession;
import com.fitradar.backend.training.repository.TrainingSessionRepository;
import com.fitradar.backend.user.model.User;
import com.fitradar.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TrainingSessionService {

    private final TrainingSessionRepository trainingSessionRepository;
    private final UserRepository userRepository;
    private final RiskPredictionService riskPredictionService;

    public TrainingSessionService(TrainingSessionRepository trainingSessionRepository,
                                  UserRepository userRepository,
                                  RiskPredictionService riskPredictionService) {
        this.trainingSessionRepository = trainingSessionRepository;
        this.userRepository = userRepository;
        this.riskPredictionService = riskPredictionService;
    }

    public TrainingSession createSession(String username, TrainingSessionRequest request) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        TrainingSession session = new TrainingSession();
        session.setUser(user);
        applyRequest(request, session);
        calculateDerivedFields(username, session);

        TrainingSession saved = trainingSessionRepository.save(session);

        // Lanza predicción ML automáticamente
        riskPredictionService.predictForSession(user, saved);

        return saved;
    }

    public TrainingSession updateSession(String username, Long id, TrainingSessionRequest request) {
        TrainingSession session = trainingSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrenamiento no encontrado"));

        if (!session.getUser().getUsername().equals(username)) {
            throw new BusinessException("No puedes editar el entrenamiento de otro usuario");
        }

        applyRequest(request, session);
        calculateDerivedFields(username, session);
        return trainingSessionRepository.save(session);
    }

    public List<TrainingSession> getSessionsByUsername(String username) {
        if (!userRepository.existsById(username))
            throw new ResourceNotFoundException("Usuario no encontrado");
        return trainingSessionRepository.findByUserUsernameOrderBySessionDateDesc(username);
    }

    public List<TrainingSession> getSessionsBetweenDates(String username,
                                                         LocalDate startDate,
                                                         LocalDate endDate) {
        if (!userRepository.existsById(username))
            throw new ResourceNotFoundException("Usuario no encontrado");
        return trainingSessionRepository
                .findByUserUsernameAndSessionDateBetweenOrderBySessionDateDesc(
                        username, startDate, endDate);
    }

    public TrainingSession getSessionById(Long id) {
        return trainingSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrenamiento no encontrado"));
    }

    public void deleteSession(String username, Long id) {
        TrainingSession session = trainingSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrenamiento no encontrado"));

        if (!session.getUser().getUsername().equals(username))
            throw new BusinessException("No puedes eliminar el entrenamiento de otro usuario");

        trainingSessionRepository.delete(session);
    }

    // ── Privado ───────────────────────────────────────────────────────────────

    private void applyRequest(TrainingSessionRequest request, TrainingSession session) {
        session.setSessionDate(request.getSessionDate());
        session.setTitle(request.getTitle());
        session.setTrainingType(request.getTrainingType());
        session.setDurationMinutes(request.getDurationMinutes());
        session.setRpe(request.getRpe());
        session.setDistanceKm(request.getDistanceKm());
        session.setAverageHeartRate(request.getAverageHeartRate());
        session.setMaxHeartRate(request.getMaxHeartRate());
        session.setCalories(request.getCalories());
        session.setNotes(request.getNotes());
        session.setIntensityLevel(deriveIntensityLevel(request.getRpe()));
    }

    private void calculateDerivedFields(String username, TrainingSession session) {
        LocalDate sessionDate = session.getSessionDate();

        int sessionLoad = session.getRpe() != null && session.getDurationMinutes() != null
                ? session.getRpe() * session.getDurationMinutes() : 0;
        session.setSessionLoad(sessionLoad);

        double acuteLoad = trainingSessionRepository.sumSessionLoadBetween(
                username, sessionDate.minusDays(7), sessionDate.minusDays(1));
        session.setAcuteLoad7d(acuteLoad + sessionLoad);

        double chronicSum = trainingSessionRepository.sumSessionLoadBetween(
                username, sessionDate.minusDays(28), sessionDate.minusDays(1));
        double chronicLoad = (chronicSum + sessionLoad) / 4.0;
        session.setChronicLoad28d(chronicLoad);

        double acwr = chronicLoad > 0 ? (acuteLoad + sessionLoad) / chronicLoad : 0.0;
        session.setAcwr(Math.round(acwr * 100.0) / 100.0);

        int daysSinceLastRest = trainingSessionRepository
                .findTopByUserUsernameAndSessionDateBeforeOrderBySessionDateDesc(
                        username, sessionDate)
                .map(prev -> (int) ChronoUnit.DAYS.between(prev.getSessionDate(), sessionDate))
                .orElse(0);
        session.setDaysSinceLastRest(daysSinceLastRest);
    }

    private TrainingSession.IntensityLevel deriveIntensityLevel(Integer rpe) {
        if (rpe == null) return TrainingSession.IntensityLevel.LOW;
        if (rpe <= 3)   return TrainingSession.IntensityLevel.LOW;
        if (rpe <= 6)   return TrainingSession.IntensityLevel.MEDIUM;
        return           TrainingSession.IntensityLevel.HIGH;
    }
}