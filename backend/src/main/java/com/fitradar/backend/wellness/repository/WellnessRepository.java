package com.fitradar.backend.wellness.repository;

import com.fitradar.backend.wellness.model.Wellness;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WellnessRepository extends JpaRepository<Wellness, Long> {

    Optional<Wellness> findByUserUsernameAndRecordDate(String username, LocalDate recordDate);

    List<Wellness> findByUserUsernameOrderByRecordDateDesc(String username);
}