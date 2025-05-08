package com.ideas2it.training.patient.vital.repository;

import com.ideas2it.training.patient.vital.entity.VitalSign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VitalSignRepository extends JpaRepository<VitalSign, Long> {
}
