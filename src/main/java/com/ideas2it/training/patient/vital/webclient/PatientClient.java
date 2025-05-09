package com.ideas2it.training.patient.vital.webclient;

import com.ideas2it.training.patient.vital.config.FeignClientConfig;
import com.ideas2it.training.patient.vital.dto.auth.LoginRequest;
import com.ideas2it.training.patient.vital.dto.auth.TokenResponse;
import com.ideas2it.training.patient.vital.dto.patient.PatientInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client to interact with the Patient service.
 */
@FeignClient(name = "patientapplication", configuration = FeignClientConfig.class)
public interface PatientClient {

    @GetMapping("/api/patients/{id}")
    PatientInfo getPatientById(@PathVariable("id") Long id);

    @PostMapping("/api/authenticate")
    TokenResponse authenticate(@RequestBody LoginRequest request);
}
