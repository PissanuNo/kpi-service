package com.kpi_service.kpi_service.app.model.dto.masterdata;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterDataRequest {

    private String dataId;
    @NotBlank
    private String dataDetailEn;
    @NotBlank
    private String dataDetailTh;
    @NotNull
    private Integer sortOrder;
    @NotBlank
    private String dataType;

    private String remark;
}
