package com.kpi_service.kpi_service.core.validation;

import com.kpi_service.kpi_service.core.model.ResponseBodyModel;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.kpi_service.kpi_service.app.constant.Constants.ResponseCode.ERROR_CODE_BUSINESS;
import static com.kpi_service.kpi_service.app.constant.Constants.ResponseMessage.ERROR_BAD_REQUEST;


@Service
public class DataValidation<T> {

    private final Validator validator;

    @Autowired
    public DataValidation(Validator validator) {
        this.validator = validator;
    }

    public ResponseEntity<ResponseBodyModel<Object>> valid(T request) {
        Set<ConstraintViolation<T>> violations = validator.validate(request);

        if (!violations.isEmpty()) {
            ResponseBodyModel<Object> res = new ResponseBodyModel<>();
            Map<String, String> errors = new HashMap<>();
            violations.forEach(constraintViolation -> {
                String fieldName = String.valueOf(constraintViolation.getPropertyPath());
                String message = constraintViolation.getMessage();
                errors.put(fieldName, message);
            });
            res.setOperationError(ERROR_CODE_BUSINESS, ERROR_BAD_REQUEST, errors);

            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
        return null;
    }
}
