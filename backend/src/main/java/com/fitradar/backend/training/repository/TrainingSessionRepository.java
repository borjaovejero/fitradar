package com.fitradar.backend.training.repository;

import com.fitradar.backend.training.model.TrainingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {

    List<TrainingSession> findByUserUsernameOrderBySessionDateDesc(String username);

    List<TrainingSession> findByUserUsernameAndSessionDateBetweenOrderBySessionDateDesc(
            String username, LocalDate startDate, LocalDate endDate);

    // Suma de sessionLoad en un rango de fechas
    // Necesario para calcular acuteLoad7d y chronicLoad28d en TrainingSessionService
    @Query("SELECT COALESCE(SUM(t.sessionLoad), 0) FROM TrainingSession t " +
            "WHERE t.user.username = :username " +
            "AND t.sessionDate BETWEEN :startDate AND :endDate")
    Double sumSessionLoadBetween(@Param("username") String username,
                                 @Param("startDate") LocalDate startDate,
                                 @Param("endDate") LocalDate endDate);

    // Sesión más reciente antes de una fecha
    // Necesario para calcular daysSinceLastRest en TrainingSessionService
    Optional<TrainingSession> findTopByUserUsernameAndSessionDateBeforeOrderBySessionDateDesc(
            String username, LocalDate date);
}