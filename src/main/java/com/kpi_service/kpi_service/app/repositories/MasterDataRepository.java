package com.kpi_service.kpi_service.app.repositories;

import com.kpi_service.kpi_service.app.model.dbs.MasterDataModel;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MasterDataRepository extends JpaRepository<MasterDataModel, String> {

    List<MasterDataModel> findByDataTypeAndDataDetailEnContainingIgnoreCaseOrDataDetailThContainingIgnoreCase(String dataType,
                                                                                                              String dataDetailEn,
                                                                                                              String dataDetailTh,
                                                                                                              Sort sort);
}