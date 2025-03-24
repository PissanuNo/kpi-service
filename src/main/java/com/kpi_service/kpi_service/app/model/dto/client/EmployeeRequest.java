package com.kpi_service.kpi_service.app.model.dto.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequest
{
    @NotBlank
    private String CompanyName;

    @NotBlank
    private String DepartmentName1;

    private String DepartmentName2;
    private String DepartmentName3;
    private String DepartmentName4;
    private String DepartmentName5;

    @NotNull
    private Integer EmployeeCode;

    private String EmployeeNo;

    @NotBlank
    private String EmployeeName;

    @NotBlank
    @Pattern(regexp = "Male|Female", message = "Gender must be either 'Male' or 'Female'")
    private String Gender;

    @NotNull
    private LocalDate BirthDate;

    @NotNull
    private LocalDate JoinDate;

    private LocalDate LastWorkingDate;

    @NotBlank
    private String JobPosition;

    @NotBlank
    private String JobGrade = "General"; // default "General"

    private String Nationality;

    private String Race;
    private String Religion;

    @NotBlank
    @Pattern(regexp = "Single|Married|Divorced|Widowed", message = "MaritalStatus must be one of {Single, Married, Divorced, Widowed}")
    private String MaritalStatus;

    @NotBlank
    @Email
    private String Email; // not duplicate

    private String EmailPersonal;
    private String ContactNo;
    private String MobileNo;
    private String EmployeeCategory;

    @NotNull
    private Integer DirectSuperior;

    private Integer Reviewer1;
    private Integer Reviewer2;
    private Integer Reviewer3;

    private String UserGroup;
    private String AccessLevel;
}
