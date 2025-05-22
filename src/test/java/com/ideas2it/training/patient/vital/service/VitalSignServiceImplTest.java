package com.ideas2it.training.patient.vital.service;

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
import com.ideas2it.training.patient.vital.service.impl.VitalSignServiceImpl;
import com.ideas2it.training.patient.vital.webclient.PatientClient;
import com.ideas2it.training.patient.vital.webclient.UserClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VitalSignServiceImplTest {

    private VitalSignServiceImpl vitalSignService;

    @Mock
    private VitalSignRepository vitalSignRepository;

    @Mock
    private VitalSignMapper mapper;

    @Mock
    private PatientClient patientClient;

    @Mock
    private UserClient userClient;

    @Mock
    private VitalKafkaProducer kafkaProducer;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        vitalSignService = new VitalSignServiceImpl(vitalSignRepository, mapper, patientClient, userClient, kafkaProducer, objectMapper);
    }

    @Test
    void testCreate_ValidRequest() throws JsonProcessingException {
        // Arrange
        VitalSignRequest request = new VitalSignRequest();
        VitalSign vital = new VitalSign();
        VitalSign savedVital = new VitalSign();
        savedVital.setPatientId(1L);
        savedVital.setDocumentedBy(1L);
        VitalSignResponse response = new VitalSignResponse();

        when(mapper.toEntity(request)).thenReturn(vital);
        when(vitalSignRepository.save(vital)).thenReturn(savedVital);
        when(userClient.getUserById(anyLong())).thenReturn(new UserInfo());
        when(patientClient.getPatientById(anyLong())).thenReturn(new PatientInfo());
        when(mapper.toResponse(savedVital)).thenReturn(response);
        when(objectMapper.writeValueAsString(response)).thenReturn("json");

        // Act
        VitalSignResponse result = vitalSignService.create(request);

        // Assert
        assertNotNull(result);
        verify(kafkaProducer, times(1)).sendVital("json");
    }

    @Test
    void testCreate_KafkaException() throws JsonProcessingException {
        // Arrange
        VitalSignRequest request = new VitalSignRequest();
        VitalSign vital = new VitalSign();
        VitalSign savedVital = new VitalSign();
        VitalSignResponse response = new VitalSignResponse();

        when(mapper.toEntity(request)).thenReturn(vital);
        when(vitalSignRepository.save(vital)).thenReturn(savedVital);
        when(mapper.toResponse(savedVital)).thenReturn(response);
        when(objectMapper.writeValueAsString(response)).thenThrow(JsonProcessingException.class);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> vitalSignService.create(request));
    }

    @Test
    void testUpdate_ValidRequest() {
        // Arrange
        Long id = 1L;
        VitalSignRequest request = new VitalSignRequest();
        VitalSign vital = new VitalSign();
        VitalSign updatedVital = new VitalSign();
        updatedVital.setDocumentedBy(1L);
        updatedVital.setPatientId(1L);
        VitalSignResponse response = new VitalSignResponse();

        when(vitalSignRepository.findById(id)).thenReturn(Optional.of(vital));
        when(vitalSignRepository.save(vital)).thenReturn(updatedVital);
        when(userClient.getUserById(anyLong())).thenReturn(new UserInfo());
        when(patientClient.getPatientById(anyLong())).thenReturn(new PatientInfo());
        when(mapper.toResponse(updatedVital)).thenReturn(response);

        // Act
        VitalSignResponse result = vitalSignService.update(id, request);

        // Assert
        assertNotNull(result);
        verify(mapper, times(1)).updateEntityFromDto(request, vital);
    }

    @Test
    void testUpdate_NonExistingId() {
        // Arrange
        Long id = 1L;
        VitalSignRequest request = new VitalSignRequest();

        when(vitalSignRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> vitalSignService.update(id, request));
    }

    @Test
    void testGetById_ExistingId() {
        // Arrange
        Long id = 1L;
        VitalSign vital = new VitalSign();
        vital.setDocumentedBy(1L);
        vital.setPatientId(1L);
        VitalSignResponse response = new VitalSignResponse();

        when(vitalSignRepository.findById(id)).thenReturn(Optional.of(vital));
        when(userClient.getUserById(anyLong())).thenReturn(new UserInfo());
        when(patientClient.getPatientById(anyLong())).thenReturn(new PatientInfo());
        when(mapper.toResponse(vital)).thenReturn(response);

        // Act
        Optional<VitalSignResponse> result = vitalSignService.getById(id);

        // Assert
        assertTrue(result.isPresent());
    }

    @Test
    void testGetById_NonExistingId() {
        // Arrange
        Long id = 1L;

        when(vitalSignRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<VitalSignResponse> result = vitalSignService.getById(id);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetAll() {
        // Arrange
        VitalSign vitalSign1=VitalSign
            .builder()
            .documentedBy(1L)
            .patientId(1L)
            .build();
        VitalSign vitalSign2=VitalSign
            .builder()
            .documentedBy(2L)
            .patientId(2L)
            .build();
        List<VitalSign> vitals = List.of(vitalSign1,vitalSign2);
        List<VitalSignResponse> responses = List.of(new VitalSignResponse(), new VitalSignResponse());

        when(vitalSignRepository.findAll()).thenReturn(vitals);
        when(userClient.getUserById(anyLong())).thenReturn(new UserInfo());
        when(patientClient.getPatientById(anyLong())).thenReturn(new PatientInfo());
        when(mapper.toResponse(any(VitalSign.class))).thenReturn(responses.get(0), responses.get(1));

        // Act
        List<VitalSignResponse> result = vitalSignService.getAll();

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    void testGetAllPaginated() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 2);
        VitalSign vitalSign1=VitalSign
            .builder()
            .documentedBy(1L)
            .patientId(1L)
            .build();
        VitalSign vitalSign2=VitalSign
            .builder()
            .documentedBy(2L)
            .patientId(2L)
            .build();
        List<VitalSign> vitals = List.of(vitalSign1,vitalSign2);
        Page<VitalSign> page = new PageImpl<>(vitals, pageRequest, 2);
        List<VitalSignResponse> responses = List.of(new VitalSignResponse(), new VitalSignResponse());
        when(vitalSignRepository.findAll(pageRequest)).thenReturn(page);
        UserInfo userInfo= UserInfo
            .builder()
            .id(1L)
            .firstName("John")
            .lastName("Doe")
            .build();
        when(userClient.getUserById(anyLong())).thenReturn(userInfo);
        PatientInfo patientInfo=PatientInfo
            .builder()
            .id(1L)
            .build();
        when(patientClient.getPatientById(anyLong())).thenReturn(patientInfo);
        when(mapper.toResponse(any(VitalSign.class))).thenReturn(responses.get(0), responses.get(1));

        // Act
        PagedResponse<VitalSignResponse> result = vitalSignService.getAllPaginated(0, 2);

        // Assert
        assertEquals(2, result.getSize());
    }

    @Test
    void testDeleteById() {
        // Arrange
        Long id = 1L;

        // Act
        vitalSignService.deleteById(id);

        // Assert
        verify(vitalSignRepository, times(1)).deleteById(id);
    }
}
