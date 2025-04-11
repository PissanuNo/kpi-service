package com.kpi_service.kpi_service.app.constant;

public class Constants {
    public enum language{
        ;
        public static final String THAI = "th";
        public static final String ENGLISH = "en";
    }

    public enum ResponseCode {
        ;
        public static final String SUCCESS_CODE = "1000";
        public static final String SUCCESS_WITH_CONDITION = "1001";
        public static final String ERROR_CODE_BUSINESS = "2000";
        public static final String ERROR_CODE_INVALID_REQUEST = "2001";
        public static final String ERROR_CODE_DATA_NOT_FOUND = "2002";
        public static final String ERROR_CODE_FILE_UPLOAD = "3001";
        public static final String ERROR_CODE_FILE_DOWNLOAD = "3002";
        public static final String FAIL_CODE_INTERNAL = "3000";
        public static final String FAIL_CODE_EXTERNAL = "4000";
        public static final String INTERNAL_SERVER_ERROR = "9999";


    }

    public enum ResponseMessage {
        ;
        public static final String SUCCESS = "Success.";
        public static final String FAILURE = "Failure.";
        public static final String ERROR = "Error.";
        public static final String DATA_NOT_FOUND = "Data not found.";
        public static final String DATA_DUPLICATE = "Data is duplicated.";
        public static final String INTERNAL_SERVER_ERROR_MSG = "Internal Server Error.";
        public static final String EXTERNAL_SERVER_ERROR_MSG = "External Server Error.";
        public static final String ERROR_FILE_STORAGE_UPLOAD = "File storage upload error.";
        public static final String ERROR_FILE_SIZE_LIMIT_EXCEEDED = "File size limit exceeded 2 mb";
        public static final String ERROR_FILE_STORAGE_DOWNLOAD = "File storage download error.";
        public static final String ERROR_FILE_NOT_FOUND = "File not found.";
        public static final String ERROR_BAD_REQUEST = "Bad request.";

    }

    public enum FormatDate {
        ;
        public static final String YYYY_MM_DD = "yyyy-MM-dd";
        public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    }

}
