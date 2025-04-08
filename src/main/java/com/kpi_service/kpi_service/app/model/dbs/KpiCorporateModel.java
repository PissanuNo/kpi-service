package com.kpi_service.kpi_service.app.model.dbs;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "kpi_corporate")
public class KpiCorporateModel {
    @Id
    private Integer kpiCorporateId;
    private String corporateId;

}
