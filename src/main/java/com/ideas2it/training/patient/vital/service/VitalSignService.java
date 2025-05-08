package com.ideas2it.training.patient.vital.service;

import com.ideas2it.training.patient.vital.dto.PagedResponse;
import com.ideas2it.training.patient.vital.dto.VitalSignRequest;
import com.ideas2it.training.patient.vital.dto.VitalSignResponse;

import java.util.List;
import java.util.Optional;

public interface VitalSignService {
    VitalSignResponse create(VitalSignRequest request);

    VitalSignResponse update(Long id, VitalSignRequest request);

    Optional<VitalSignResponse> getById(Long id);

    List<VitalSignResponse> getAll();

    PagedResponse<VitalSignResponse> getAllPaginated(int offset, int limit);

    void deleteById(Long id);
}

