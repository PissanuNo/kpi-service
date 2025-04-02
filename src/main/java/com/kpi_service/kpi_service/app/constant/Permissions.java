package com.kpi_service.kpi_service.app.constant;

public class Permissions {

    public enum permissionFlag {
        ;
        public static final int READ = 1;
        public static final int SEARCH = 2;
        public static final int CREATE = 4;
        public static final int UPDATE = 8;
        public static final int EXPORT = 16;
        public static final int IMPORT = 32;
        public static final int ACTIVE_INACTIVE = 64;
        public static final int DELETE = 128;
    }

    public enum menuCode {
        ;
        public static final String KPI_MANAGEMENT = "KPI_MANAGEMENT";
    }
}

