package com.kpi_service.kpi_service.app.model.dbs;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "kpi_master_data")
public class MasterDataModel {
    @Id
    private String dataId;
    private String dataDetailEn;
    private String dataDetailTh;
    private Integer sortOrder;
    private String dataType;
    private String remark;

}
