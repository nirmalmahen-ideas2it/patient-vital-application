package com.ideas2it.training.patient.vital.repository;

import com.ideas2it.training.patient.vital.entity.VitalSign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ImportAutoConfiguration(exclude = {LiquibaseAutoConfiguration.class})
@TestPropertySource(properties = "VAULT_URL=http://localhost:8200")

class VitalSignRepositoryTest {

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:testdb");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");
    }

    @Autowired
    private VitalSignRepository vitalSignRepository;

    private VitalSign vitalSign;

    @BeforeEach
    void setUp() {
        vitalSign = new VitalSign();
        vitalSign.setPatientId(1L);
        vitalSign.setPulse(80);
        vitalSign.setDocumentedBy(1L);
        vitalSign.setCreatedDate(LocalDateTime.now().toInstant(ZoneOffset.MIN));
    }

    @Test
    void testSave_ValidEntity() {
        // Act
        VitalSign saved = vitalSignRepository.save(vitalSign);

        // Assert
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(80, saved.getPulse());
    }

    @Test
    void testSave_NullEntity() {
        // Act & Assert
        assertThrows(InvalidDataAccessApiUsageException.class, () -> vitalSignRepository.save(null));
    }

    @Test
    void testFindById_ExistingId() {
        // Arrange
        VitalSign saved = vitalSignRepository.save(vitalSign);

        // Act
        Optional<VitalSign> found = vitalSignRepository.findById(saved.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    void testFindById_NonExistingId() {
        // Act
        Optional<VitalSign> found = vitalSignRepository.findById(999L);

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void testFindById_NullId() {
        // Act & Assert
        assertThrows(InvalidDataAccessApiUsageException.class, () -> vitalSignRepository.findById(null));
    }

    @Test
    void testDeleteById_ExistingId() {
        // Arrange
        VitalSign saved = vitalSignRepository.save(vitalSign);

        // Act
        vitalSignRepository.deleteById(saved.getId());

        // Assert
        Optional<VitalSign> found = vitalSignRepository.findById(saved.getId());
        assertFalse(found.isPresent());
    }

    @Test
    void testDeleteById_NonExistingId() {
        // Act & Assert
        assertDoesNotThrow(() -> vitalSignRepository.deleteById(999L));
    }

    @Test
    void testFindAll_PopulatedDatabase() {
        // Arrange
        vitalSignRepository.save(vitalSign);
        VitalSign anotherVitalSign = new VitalSign();
        anotherVitalSign.setPatientId(2L);
        anotherVitalSign.setPulse(90);
        anotherVitalSign.setDocumentedBy(2L);
        anotherVitalSign.setCreatedDate(LocalDateTime.now().toInstant(ZoneOffset.MIN));
        vitalSignRepository.save(anotherVitalSign);

        // Act
        List<VitalSign> all = vitalSignRepository.findAll();

        // Assert
        assertEquals(5, all.size());
    }

    @Test
    void testFindTopByPatientIdOrderByCreatedDateDesc() {
        // Act
        VitalSign latestVital = vitalSignRepository.findTopByPatientIdOrderByCreatedDateDesc(1L);

        // Assert
        assertNotNull(latestVital);
        assertEquals(80, latestVital.getPulse());
    }
}
