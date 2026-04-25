package com.demo.jsondemo.config;

import lombok.Data;

@Data
public class DatabaseConfig {
    private boolean enabled;
    private String url;
    private String username;
    private String password;
}
