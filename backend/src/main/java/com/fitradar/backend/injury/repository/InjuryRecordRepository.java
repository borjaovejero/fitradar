package com.fitradar.backend.injury.repository;

import com.fitradar.backend.injury.model.InjuryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InjuryRecordRepository extends JpaRepository<InjuryRecord, Long> {

    List<InjuryRecord> findByUserUsernameOrderByStartDateDesc(String username);

    // Para el dashboard: saber si el usuario tiene alguna lesión activa
    boolean existsByUserUsernameAndFullyRecoveredFalse(String username);

    // Para el ML: lesiones pendientes de reportar al modelo
    List<InjuryRecord> findByUserUsernameAndReportedToModelFalse(String username);
}