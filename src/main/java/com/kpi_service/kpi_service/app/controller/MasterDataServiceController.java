package com.kpi_service.kpi_service.app.controller;

import com.kpi_service.kpi_service.app.model.dbs.MasterDataModel;
import com.kpi_service.kpi_service.app.service.MasterDataService;
import com.kpi_service.kpi_service.core.model.ResponseBodyModel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1")
public class MasterDataServiceController {

    private final MasterDataService masterDataService;

    @GetMapping(path = "/master-data",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyModel<List<MasterDataModel>>> getMasterData(@RequestParam("dataDetail") String dataDetail,
                                                                          @RequestParam("dataType") String dataType) {
        ResponseBodyModel<List<MasterDataModel>> response = masterDataService.getMasterData(dataDetail, dataType);
        return ResponseEntity.ok(response);
    }

}
