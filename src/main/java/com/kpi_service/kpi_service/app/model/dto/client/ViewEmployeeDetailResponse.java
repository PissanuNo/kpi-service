package com.kpi_service.kpi_service.app.model.dto.client;

import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewEmployeeDetailResponse {
    private String corporateId;
    private String corporateNameEn;
    private String departmentLevel0;
    private String departmentLevel1;
    private String departmentLevel2;
    private String departmentLevel3;
    private String departmentLevel4;
    private String employeeId;
    private String employeeNo;
    private String employeeName;
    private String gender;
    private Date birthDate;
    private Date joinDate;
    private Date lastWorkingDate;
    private String jobPositionNameEn;
    private String jobGradeNameEn;
    private String nationalityNameEn;
    private String raceNameEn;
    private String religionNameEn;
    private String marital;
    private String email;
    private String personalEmail;
    private String mobileContactNo;
    private String contactNo;
    private String employeeCategory;
    private String directSuperiorId;
    private String reviewer1Id;
    private String reviewer2Id;
    private String reviewer3Id;
}
