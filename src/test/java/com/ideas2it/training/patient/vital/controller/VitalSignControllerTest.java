package com.ideas2it.training.patient.vital.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideas2it.training.patient.vital.dto.PagedResponse;
import com.ideas2it.training.patient.vital.dto.VitalSignRequest;
import com.ideas2it.training.patient.vital.dto.VitalSignResponse;
import com.ideas2it.training.patient.vital.service.VitalSignService;
import com.ideas2it.training.patient.vital.web.rest.controller.VitalSignController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = VitalSignController.class,
    excludeAutoConfiguration = {OAuth2ResourceServerAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)  // disables security filters for test
@WithMockUser(roles = "USER")
@TestPropertySource(properties = "VAULT_URL=http://localhost:8200")
class VitalSignControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VitalSignService vitalSignService;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private RedisTemplate <String,Object> redisTemplate;

    private VitalSignRequest vitalSignRequest;
    private VitalSignResponse vitalSignResponse;

    @BeforeEach
    public void setup() throws Exception {
        Jwt jwt = Mockito.mock(Jwt.class);
        Mockito.when(jwt.hasClaim(any(String.class))).thenReturn(false);
        Mockito.when(jwtDecoder.decode(any(String.class))).thenReturn(jwt);

        vitalSignRequest = new VitalSignRequest();
        vitalSignRequest.setPulse(80);
        vitalSignRequest.setPatientId(1L);

        vitalSignResponse = VitalSignResponse.builder()
            .id(1L)
            .pulse(80)
            .build();
    }

    @Test
    void testCreate() throws Exception {
        Mockito.when(vitalSignService.create(any(VitalSignRequest.class))).thenReturn(vitalSignResponse);

        mockMvc.perform(post("/api/vital-signs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vitalSignRequest)))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(vitalSignResponse)));
    }

    @Test
    void testUpdate() throws Exception {
        Long id = 1L;
        Mockito.when(vitalSignService.update(eq(id), any(VitalSignRequest.class))).thenReturn(vitalSignResponse);

        mockMvc.perform(put("/api/vital-signs/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vitalSignRequest)))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(vitalSignResponse)));
    }

    @Test
    void testGetById() throws Exception {
        Long id = 1L;
        Mockito.when(vitalSignService.getById(id)).thenReturn(java.util.Optional.of(vitalSignResponse));

        mockMvc.perform(get("/api/vital-signs/{id}", id))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(vitalSignResponse)));
    }

    @Test
    void testGetById_NotFound() throws Exception {
        Long id = 1L;
        Mockito.when(vitalSignService.getById(id)).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/api/vital-signs/{id}", id))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGetAll() throws Exception {
        List<VitalSignResponse> response = Collections.singletonList(vitalSignResponse);
        Mockito.when(vitalSignService.getAll()).thenReturn(response);

        mockMvc.perform(get("/api/vital-signs"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void testGetAllPaginated() throws Exception {
        int offset = 0;
        int limit = 10;
        PagedResponse<VitalSignResponse> response = new PagedResponse<>();
        response.setSize(1);
        response.setPage(0);
        response.setItems(Collections.singletonList(vitalSignResponse));

        Mockito.when(vitalSignService.getAllPaginated(offset, limit)).thenReturn(response);

        mockMvc.perform(get("/api/vital-signs/paginated")
                .param("offset", String.valueOf(offset))
                .param("limit", String.valueOf(limit))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void testDelete() throws Exception {
        Long id = 1L;
        Mockito.doNothing().when(vitalSignService).deleteById(id);

        mockMvc.perform(delete("/api/vital-signs/{id}", id))
            .andExpect(status().isNoContent());
    }
}
