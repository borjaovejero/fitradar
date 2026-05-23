package com.fitradar.backend.wellness.service;

import com.fitradar.backend.shared.exception.BusinessException;
import com.fitradar.backend.shared.exception.ResourceNotFoundException;
import com.fitradar.backend.user.model.User;
import com.fitradar.backend.user.repository.UserRepository;
import com.fitradar.backend.wellness.dto.WellnessRequest;
import com.fitradar.backend.wellness.model.Wellness;
import com.fitradar.backend.wellness.repository.WellnessRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class WellnessService {

    private final WellnessRepository wellnessRepository;
    private final UserRepository userRepository;

    public WellnessService(WellnessRepository wellnessRepository,
                           UserRepository userRepository) {
        this.wellnessRepository = wellnessRepository;
        this.userRepository = userRepository;
    }

    public Wellness createRecord(String username, WellnessRequest request) {
        // Fecha = hoy, obtenida en el backend
        LocalDate today = LocalDate.now();

        if (wellnessRepository.findByUserUsernameAndRecordDate(username, today).isPresent()) {
            throw new BusinessException("Ya existe un registro de bienestar para hoy");
        }

        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Wellness record = new Wellness();
        record.setUser(user);
        record.setRecordDate(today);
        applyRequest(request, record);

        return wellnessRepository.save(record);
    }

    public Wellness updateRecord(String username, Long id, WellnessRequest request) {
        Wellness record = wellnessRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro no encontrado"));

        if (!record.getUser().getUsername().equals(username)) {
            throw new BusinessException("No puedes editar el registro de otro usuario");
        }

        applyRequest(request, record);
        return wellnessRepository.save(record);
    }

    public List<Wellness> getRecordsByUsername(String username) {
        if (!userRepository.existsById(username)) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }
        return wellnessRepository.findByUserUsernameOrderByRecordDateDesc(username);
    }

    private void applyRequest(WellnessRequest request, Wellness record) {
        record.setSleepHours(request.getSleepHours());
        record.setSleepQuality(request.getSleepQuality());
        record.setFatigue(request.getFatigue());
        record.setStress(request.getStress());
        record.setMuscleSoreness(request.getMuscleSoreness());
        record.setGeneralFeeling(request.getGeneralFeeling());
        record.setRecoveryFeeling(request.getRecoveryFeeling());
        record.setHrvRmssd(request.getHrvRmssd());
        record.setRestingHr(request.getRestingHr());
    }
}