package com.kpi_service.kpi_service.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtClaim {
    @Setter
    private String token;
    @Setter
    private Long expire;
    @Setter
    private Long iat;
    @Setter
    private Map<String,Object> attrs;

}