package com.demo.jsondemo.config;

import lombok.Data;

@Data
public class ApiConfig {
    private ApiSettings api;
    private DatabaseConfig database;
}

@Data
class ApiSettings {
    private String baseUrl;
    private int timeout;
}
