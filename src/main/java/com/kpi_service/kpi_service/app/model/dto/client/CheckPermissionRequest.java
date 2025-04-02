package com.kpi_service.kpi_service.app.model.dto.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckPermissionRequest {
    @NotBlank
    private String token;
    @NotBlank
    private String menu;
    @NotNull
    private int permission;
}
