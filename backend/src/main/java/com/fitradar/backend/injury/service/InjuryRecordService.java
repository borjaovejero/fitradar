package com.fitradar.backend.injury.service;

import com.fitradar.backend.injury.dto.InjuryRecordRequest;
import com.fitradar.backend.injury.model.InjuryRecord;
import com.fitradar.backend.injury.repository.InjuryRecordRepository;
import com.fitradar.backend.shared.exception.BusinessException;
import com.fitradar.backend.shared.exception.ResourceNotFoundException;
import com.fitradar.backend.user.model.User;
import com.fitradar.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class InjuryRecordService {

    private final InjuryRecordRepository injuryRecordRepository;
    private final UserRepository userRepository;

    public InjuryRecordService(InjuryRecordRepository injuryRecordRepository,
                               UserRepository userRepository) {
        this.injuryRecordRepository = injuryRecordRepository;
        this.userRepository = userRepository;
    }

    public InjuryRecord createInjuryRecord(String username, InjuryRecordRequest request) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        InjuryRecord record = new InjuryRecord();
        record.setUser(user);
        applyRequest(record, request);

        record.setReportedToModel(true);
        return injuryRecordRepository.save(record);
    }

    public List<InjuryRecord> getByUsername(String username) {
        if (!userRepository.existsById(username)) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }
        return injuryRecordRepository.findByUserUsernameOrderByStartDateDesc(username);
    }

    public InjuryRecord getById(Long id) {
        return injuryRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesión no encontrada"));
    }

    public InjuryRecord updateInjuryRecord(String username, Long id, InjuryRecordRequest request) {
        InjuryRecord record = getById(id);

        if (!record.getUser().getUsername().equals(username)) {
            throw new BusinessException("No puedes editar la lesión de otro usuario");
        }

        applyRequest(record, request);
        return injuryRecordRepository.save(record);
    }

    public void deleteInjuryRecord(String username, Long id) {
        InjuryRecord record = getById(id);

        if (!record.getUser().getUsername().equals(username)) {
            throw new BusinessException("No puedes eliminar la lesión de otro usuario");
        }

        injuryRecordRepository.delete(record);
    }

    public boolean hasActiveInjury(String username) {
        return injuryRecordRepository.existsByUserUsernameAndFullyRecoveredFalse(username);
    }

    // Llamado por RiskPredictionService tras enviar la lesión al modelo ML
    public void markAsReportedToModel(Long id) {
        InjuryRecord record = getById(id);
        record.setReportedToModel(true);
        injuryRecordRepository.save(record);
    }

    // ── Privado ───────────────────────────────────────────────────────────────

    private void applyRequest(InjuryRecord record, InjuryRecordRequest request) {
        record.setInjuryType(request.getInjuryType());
        record.setBodyZone(request.getBodyZone());
        record.setInjurySeverity(request.getInjurySeverity());
        record.setStartDate(request.getStartDate());
        record.setEndDate(request.getEndDate());
        record.setRecurrence(request.isRecurrence());
        record.setFullyRecovered(request.isFullyRecovered());
        record.setDescription(request.getDescription());

        // daysOff calculado automáticamente si hay fecha de fin
        if (request.getStartDate() != null && request.getEndDate() != null) {
            int days = (int) ChronoUnit.DAYS.between(
                    request.getStartDate(), request.getEndDate());
            record.setDaysOff(days);
        } else {
            record.setDaysOff(null);
        }
    }
}