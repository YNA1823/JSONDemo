package com.demo.jsondemo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private static ConfigManager instance;
    private ApiConfig config;

    private ConfigManager() {
        loadConfig();
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    private void loadConfig() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            String configPath = System.getProperty("config.path",
                    "src/main/resources/config/config.yaml");
            config = mapper.readValue(new File(configPath), ApiConfig.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    public String getBaseUrl() {
        return config.getApi().getBaseUrl();
    }

    public int getTimeout() {
        return config.getApi().getTimeout();
    }

    public DatabaseConfig getDatabaseConfig() {
        return config.getDatabase();
    }
}
