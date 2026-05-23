package com.fitradar.backend.risk.repository;

import com.fitradar.backend.risk.model.RiskPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RiskPredictionRepository extends JpaRepository<RiskPrediction, Long> {

    List<RiskPrediction> findByUserUsernameOrderByPredictionDateDesc(String username);

    Optional<RiskPrediction> findTopByUserUsernameOrderByPredictionDateDesc(String username);

    Optional<RiskPrediction> findByTrainingSessionId(Long trainingSessionId);
}