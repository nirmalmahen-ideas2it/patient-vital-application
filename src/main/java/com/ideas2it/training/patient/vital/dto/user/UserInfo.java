package com.ideas2it.training.patient.vital.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class UserInfo {

    private final Long id;
    private final String firstName;
    private final String lastName;

}
