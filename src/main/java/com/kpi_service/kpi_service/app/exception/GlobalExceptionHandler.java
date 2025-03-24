package com.kpi_service.kpi_service.app.exception;


import com.kpi_service.kpi_service.core.model.ResponseBodyModel;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

import static com.kpi_service.kpi_service.app.constant.Constants.ResponseCode.ERROR_CODE_INVALID_REQUEST;
import static com.kpi_service.kpi_service.app.constant.Constants.ResponseMessage.ERROR_BAD_REQUEST;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseBodyModel<List<String>>> notValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ResponseBodyModel<List<String>> response = new ResponseBodyModel<>();
        ex.getAllErrors().forEach(err ->
                log.error(String.format("Invalid : %s",err.getDefaultMessage()))
                );

        response.setOperationError(ERROR_CODE_INVALID_REQUEST,ERROR_BAD_REQUEST,null);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
