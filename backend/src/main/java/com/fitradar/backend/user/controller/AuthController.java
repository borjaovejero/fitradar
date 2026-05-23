package com.fitradar.backend.user.controller;

import com.fitradar.backend.shared.exception.BusinessException;
import com.fitradar.backend.user.dto.LoginRequest;
import com.fitradar.backend.user.dto.LoginResponse;
import com.fitradar.backend.user.dto.RegisterRequest;
import com.fitradar.backend.user.dto.UpdateUserRequest;
import com.fitradar.backend.user.dto.UserResponse;
import com.fitradar.backend.user.model.User;
import com.fitradar.backend.user.service.AuthService;
import com.fitradar.backend.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    public AuthController(UserService userService,
                          AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        User createdUser = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(createdUser));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = authService.login(request);
        LoginResponse response = new LoginResponse("Login correcto", user.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String username,
                                                Authentication authentication) {
        assertOwnUser(username, authentication);

        User user = userService.getByUsername(username);
        return ResponseEntity.ok(mapToResponse(user));
    }

    @PutMapping("/users/{username}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable String username,
                                                   @Valid @RequestBody UpdateUserRequest request,
                                                   Authentication authentication) {
        assertOwnUser(username, authentication);

        User user = userService.updateProfile(username, request);
        return ResponseEntity.ok(mapToResponse(user));
    }

    private void assertOwnUser(String username, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new BusinessException("No hay usuario autenticado");
        }

        if (!authentication.getName().equals(username)) {
            throw new BusinessException("No puedes acceder al perfil de otro usuario");
        }
    }

    private UserResponse mapToResponse(User user) {
        UserResponse r = new UserResponse();
        r.setUsername(user.getUsername());
        r.setEmail(user.getEmail());
        r.setFirstName(user.getFirstName());
        r.setLastName(user.getLastName());
        r.setBirthDate(user.getBirthDate());
        r.setSex(user.getSex());
        r.setEnabled(user.isEnabled());
        r.setHeightCm(user.getHeightCm());
        r.setWeightKg(user.getWeightKg());
        r.setSportType(user.getSportType());
        r.setAthleteLevel(user.getAthleteLevel());
        r.setWeeklyTrainingDays(user.getWeeklyTrainingDays());
        r.setPreviousInjury(user.isPreviousInjury());
        r.setObservations(user.getObservations());
        return r;
    }
}
