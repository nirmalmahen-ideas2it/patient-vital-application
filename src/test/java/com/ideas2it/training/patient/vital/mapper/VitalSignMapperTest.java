package com.ideas2it.training.patient.vital.mapper;

import com.ideas2it.training.patient.vital.dto.VitalSignRequest;
import com.ideas2it.training.patient.vital.dto.VitalSignResponse;
import com.ideas2it.training.patient.vital.entity.VitalSign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class VitalSignMapperTest {

    private VitalSignMapper vitalSignMapper;

    @BeforeEach
    void setUp() {
        vitalSignMapper = Mappers.getMapper(VitalSignMapper.class);
    }

    @Test
    void testToResponse_ValidInput() {
        // Arrange
        VitalSign vitalSign = new VitalSign();
        vitalSign.setId(1L);
        vitalSign.setPulse(80);

        // Act
        VitalSignResponse response = vitalSignMapper.toResponse(vitalSign);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(80, response.getPulse());
        assertNull(response.getPatient()); // Ignored field
        assertNull(response.getDocumentedBy()); // Ignored field
    }

    @Test
    void testToResponse_NullInput() {
        // Act
        VitalSignResponse response = vitalSignMapper.toResponse(null);

        // Assert
        assertNull(response);
    }

    @Test
    void testToEntity_ValidInput() {
        // Arrange
        VitalSignRequest request = new VitalSignRequest();
        request.setPulse(90);

        // Act
        VitalSign entity = vitalSignMapper.toEntity(request);

        // Assert
        assertNotNull(entity);
        assertEquals(90, entity.getPulse());
    }

    @Test
    void testToEntity_NullInput() {
        // Act
        VitalSign entity = vitalSignMapper.toEntity(null);

        // Assert
        assertNull(entity);
    }

    @Test
    void testUpdateEntityFromDto_ValidInput() {
        // Arrange
        VitalSignRequest request = new VitalSignRequest();
        request.setPulse(100);

        VitalSign entity = new VitalSign();
        entity.setPulse(80);

        // Act
        vitalSignMapper.updateEntityFromDto(request, entity);

        // Assert
        assertNotNull(entity);
        assertEquals(100, entity.getPulse()); // Updated field
    }

    @Test
    void testUpdateEntityFromDto_NullDto() {
        // Arrange
        VitalSign entity = new VitalSign();
        entity.setPulse(80);

        // Act
        vitalSignMapper.updateEntityFromDto(null, entity);

        // Assert
        assertNotNull(entity);
        assertEquals(80, entity.getPulse()); // Unchanged
    }

    @Test
    void testUpdateEntityFromDto_NullEntity() {
        // Arrange
        VitalSignRequest request = new VitalSignRequest();
        request.setPulse(100);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> vitalSignMapper.updateEntityFromDto(request, null));
    }
}
