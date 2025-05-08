package com.ideas2it.training.patient.vital.mapper;

import com.ideas2it.training.patient.vital.dto.VitalSignRequest;
import com.ideas2it.training.patient.vital.dto.VitalSignResponse;
import com.ideas2it.training.patient.vital.entity.VitalSign;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface VitalSignMapper {

    @Mapping(target = "patient", ignore = true)  // Ignore patientId during mapping
    @Mapping(target = "documentedBy", ignore = true)
        // Ignore documentedBy during mapping
    VitalSignResponse toResponse(VitalSign vitalSign);

    VitalSign toEntity(VitalSignRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(VitalSignRequest dto, @MappingTarget VitalSign entity);
}


