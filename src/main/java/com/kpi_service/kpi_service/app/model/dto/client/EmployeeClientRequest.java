package com.kpi_service.kpi_service.app.model.dto.client;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kpi_service.kpi_service.app.utils.MicrosoftDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import static com.kpi_service.kpi_service.app.constant.Kpi.JobGrad.GENERAL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeClientRequest {

    @JsonProperty("CompanyName")
    private String companyName;

    @JsonProperty("DepartmentName1")
    private String departmentName1;

    @JsonProperty("DepartmentName2")
    private String departmentName2;

    @JsonProperty("DepartmentName3")
    private String departmentName3;

    @JsonProperty("DepartmentName4")
    private String departmentName4;

    @JsonProperty("DepartmentName5")
    private String departmentName5;

    @JsonProperty("EmployeeCode")
    private Integer employeeCode;

    @JsonProperty("EmployeeNo")
    private String employeeNo;

    @JsonProperty("EmployeeName")
    private String employeeName;

    @JsonProperty("Gender")
    private String gender;

    @JsonProperty("BirthDate")
    @JsonSerialize(using = MicrosoftDateSerializer.class)
    private Date birthDate;

    @JsonProperty("JoinDate")
    @JsonSerialize(using = MicrosoftDateSerializer.class)
    private Date joinDate;

    @JsonProperty("LastWorkingDate")
    @JsonSerialize(using = MicrosoftDateSerializer.class)
    private Date lastWorkingDate;

    @JsonProperty("JobPosition")
    private String jobPosition;

    @JsonProperty("JobGrade")
    @Builder.Default
    private String jobGrade = GENERAL; // default "General"

    @JsonProperty("Nationality")
    private String nationality;

    @JsonProperty("Race")
    private String race;

    @JsonProperty("Religion")
    private String religion;

    @JsonProperty("MaritalStatus")
    private String maritalStatus;

    @JsonProperty("Email")
    private String email; // not duplicate

    @JsonProperty("EmailPersonal")
    private String emailPersonal;

    @JsonProperty("ContactNo")
    private String contactNo;

    @JsonProperty("MobileNo")
    private String mobileNo;

    @JsonProperty("EmployeeCategory")
    private String employeeCategory;

    @JsonProperty("DirectSuperior")
    private Integer directSuperior;

    @JsonProperty("Reviewer1")
    private Integer reviewer1;

    @JsonProperty("Reviewer2")
    private Integer reviewer2;

    @JsonProperty("Reviewer3")
    private Integer reviewer3;

}
