package com.ideas2it.training.patient.vital.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideas2it.training.patient.vital.dto.PagedResponse;
import com.ideas2it.training.patient.vital.dto.VitalSignRequest;
import com.ideas2it.training.patient.vital.dto.VitalSignResponse;
import com.ideas2it.training.patient.vital.dto.patient.PatientInfo;
import com.ideas2it.training.patient.vital.dto.user.UserInfo;
import com.ideas2it.training.patient.vital.entity.VitalSign;
import com.ideas2it.training.patient.vital.mapper.VitalSignMapper;
import com.ideas2it.training.patient.vital.producer.VitalKafkaProducer;
import com.ideas2it.training.patient.vital.repository.VitalSignRepository;
import com.ideas2it.training.patient.vital.service.VitalSignService;
import com.ideas2it.training.patient.vital.webclient.PatientClient;
import com.ideas2it.training.patient.vital.webclient.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class VitalSignServiceImpl implements VitalSignService {

    private final VitalSignRepository vitalSignRepository;
    private final VitalSignMapper mapper;
    private final PatientClient patientClient;
    private final UserClient userClient;
    private final VitalKafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    @Override
    public VitalSignResponse create(VitalSignRequest request) {
        try {
            VitalSign vital = mapper.toEntity(request);
            VitalSign saved = vitalSignRepository.save(vital);
            VitalSignResponse response = enrichWithExternalData(saved);
            kafkaProducer.sendVital(objectMapper.writeValueAsString(response));
            return response;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public VitalSignResponse update(Long id, VitalSignRequest request) {
        VitalSign vital = vitalSignRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Vital sign not found: " + id));
        mapper.updateEntityFromDto(request, vital);
        VitalSign updated = vitalSignRepository.save(vital);
        return enrichWithExternalData(updated);
    }

    @Override
    public Optional<VitalSignResponse> getById(Long id) {
        return vitalSignRepository.findById(id)
            .map(this::enrichWithExternalData);
    }

    @Override
    public List<VitalSignResponse> getAll() {
        return vitalSignRepository.findAll().stream()
            .map(this::enrichWithExternalData)
            .collect(Collectors.toList());
    }

    @Override
    public PagedResponse<VitalSignResponse> getAllPaginated(int offset, int limit) {
        var pageRequest = PageRequest.of(offset, limit);
        var page = vitalSignRepository.findAll(pageRequest);
        List<VitalSignResponse> responses = page.getContent().stream()
            .map(this::enrichWithExternalData)
            .collect(Collectors.toList());
        return new PagedResponse<>(
            responses,
            page.getTotalElements(),
            page.getNumber(),
            page.getSize()
        );
    }

    @Override
    public void deleteById(Long id) {
        vitalSignRepository.deleteById(id);
    }

    private VitalSignResponse enrichWithExternalData(VitalSign vital) {
        VitalSignResponse response = mapper.toResponse(vital);
        UserInfo userInfo = userClient.getUserById(vital.getDocumentedBy());
        response.setDocumentedBy(new UserInfo(userInfo.getId(), userInfo.getFirstName(), userInfo.getLastName()));
        PatientInfo patientInfo = patientClient.getPatientById(vital.getPatientId());
        response.setPatient(patientInfo);
        return response;
    }
}


