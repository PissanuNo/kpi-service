package com.kpi_service.kpi_service.app.repositories;

import com.kpi_service.kpi_service.app.model.dbs.KpiEmployeeModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KpiEmployeeRepository extends JpaRepository<KpiEmployeeModel, String> {

    Optional<KpiEmployeeModel> findByEmployeeId(String employeeId);
}