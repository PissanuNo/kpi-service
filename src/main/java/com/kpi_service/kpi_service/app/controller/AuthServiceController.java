package com.kpi_service.kpi_service.app.controller;

import com.kpi_service.kpi_service.app.model.dto.AuthRequest;
import com.kpi_service.kpi_service.app.model.dto.AuthResponse;
import com.kpi_service.kpi_service.app.service.AuthService;
import com.kpi_service.kpi_service.core.model.ResponseBodyModel;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1")
public class AuthServiceController {

    private final AuthService authService;

    public AuthServiceController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(path = "/auth",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyModel<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        ResponseBodyModel<AuthResponse> response = authService.login(request);
        return ResponseEntity.ok(response);
    }

}
