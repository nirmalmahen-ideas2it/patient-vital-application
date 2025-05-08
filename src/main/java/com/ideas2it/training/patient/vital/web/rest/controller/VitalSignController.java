package com.ideas2it.training.patient.vital.web.rest.controller;

import com.ideas2it.training.patient.vital.dto.VitalSignRequest;
import com.ideas2it.training.patient.vital.dto.VitalSignResponse;
import com.ideas2it.training.patient.vital.service.VitalSignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vital-signs")
@RequiredArgsConstructor
public class VitalSignController {

    private final VitalSignService service;

    @PostMapping
    @Operation(summary = "Create a new vital sign record")
    public ResponseEntity<VitalSignResponse> create(@RequestBody VitalSignRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing vital sign record")
    public ResponseEntity<VitalSignResponse> update(@PathVariable Long id,
                                                    @RequestBody VitalSignRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vital sign by ID")
    public ResponseEntity<VitalSignResponse> getById(@PathVariable Long id) {
        return service.getById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all vital signs")
    public ResponseEntity<List<VitalSignResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/paginated")
    @Operation(summary = "Get all vital signs with pagination")
    public ResponseEntity<?> getAllPaginated(int offset, int limit) {
        return ResponseEntity.ok(service.getAllPaginated(offset, limit));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete vital sign by ID")
    @ApiResponse(responseCode = "204", description = "Deleted successfully")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

