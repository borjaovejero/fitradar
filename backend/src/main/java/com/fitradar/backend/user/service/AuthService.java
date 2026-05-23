package com.fitradar.backend.user.service;

import com.fitradar.backend.shared.exception.BusinessException;
import com.fitradar.backend.user.dto.LoginRequest;
import com.fitradar.backend.user.model.User;
import com.fitradar.backend.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User login(LoginRequest request) {
        User user = userRepository.findById(request.getUsername())
                .orElseThrow(() -> new BusinessException("Credenciales incorrectas"));

        if (!user.isEnabled()) {
            throw new BusinessException("Usuario deshabilitado");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("Credenciales incorrectas");
        }

        return user;
    }
}