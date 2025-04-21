package com.kpi_service.kpi_service.app.model.dto.masterdata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterDataResponse {

    private String dataId;

    private String dataDetailEn;

    private String dataDetailTh;

    private Integer sortOrder;

    private String dataType;

    private String remark;
}
