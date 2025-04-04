package com.kpi_service.kpi_service.app.service;


import com.kpi_service.kpi_service.app.model.dbs.MasterDataModel;
import com.kpi_service.kpi_service.core.model.ResponseBodyModel;

import java.util.List;

public interface MasterDataService {

    ResponseBodyModel<List<MasterDataModel>> getMasterData(String dataDetail, String dataType);
}
