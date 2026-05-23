package com.fitradar.backend.user.service;

import com.fitradar.backend.shared.exception.BusinessException;
import com.fitradar.backend.shared.exception.ResourceNotFoundException;
import com.fitradar.backend.user.dto.RegisterRequest;
import com.fitradar.backend.user.dto.UpdateUserRequest;
import com.fitradar.backend.user.model.User;
import com.fitradar.backend.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("El nombre de usuario ya existe");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("El email ya existe");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setBirthDate(request.getBirthDate());
        user.setSex(request.getSex());
        user.setEnabled(true);

        // Perfil deportivo (opcionales)
        user.setHeightCm(request.getHeightCm());
        user.setWeightKg(request.getWeightKg());
        user.setSportType(request.getSportType());
        user.setAthleteLevel(request.getAthleteLevel());
        user.setWeeklyTrainingDays(request.getWeeklyTrainingDays());
        user.setPreviousInjury(request.isPreviousInjury());
        user.setObservations(request.getObservations());

        return userRepository.save(user);
    }

    public User getByUsername(String username) {
        return userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    public User updateProfile(String username, UpdateUserRequest request) {
        User user = getByUsername(username);

        userRepository.findByEmail(request.getEmail())
                .filter(existing -> !existing.getUsername().equals(username))
                .ifPresent(existing -> { throw new BusinessException("El email ya existe"); });

        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setBirthDate(request.getBirthDate());
        user.setSex(request.getSex());

        // Perfil deportivo
        user.setHeightCm(request.getHeightCm());
        user.setWeightKg(request.getWeightKg());
        user.setSportType(request.getSportType());
        user.setAthleteLevel(request.getAthleteLevel());
        user.setWeeklyTrainingDays(request.getWeeklyTrainingDays());
        user.setPreviousInjury(request.isPreviousInjury());
        user.setObservations(request.getObservations());

        return userRepository.save(user);
    }
}