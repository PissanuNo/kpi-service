package com.kpi_service.kpi_service.app.model.dbs;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.Date;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "kpi_employee")
public class KpiEmployeeModel {
    @Id
    private String transId;
    private String employeeId;
    private String accessLevel;
    private String userGroup;

    @Column(columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    @Builder.Default
    private Date createDate =  new Timestamp(System.currentTimeMillis());
    @Builder.Default
    private String createBy = "Administrator";
    @Column(columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    @Builder.Default
    private Date modifyDate = new Timestamp(System.currentTimeMillis());
    @Builder.Default
    private String modifyBy = "Administrator";

}
