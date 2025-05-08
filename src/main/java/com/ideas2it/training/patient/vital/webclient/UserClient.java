package com.ideas2it.training.patient.vital.webclient;

import com.ideas2it.training.patient.vital.config.FeignClientConfig;
import com.ideas2it.training.patient.vital.dto.user.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client to interact with the User service.
 */
@FeignClient(name = "user-service", url = "http://localhost:9090", configuration = FeignClientConfig.class)
public interface UserClient {

    @GetMapping("/api/users/{id}")
    UserInfo getUserById(@PathVariable("id") Long id);
}

