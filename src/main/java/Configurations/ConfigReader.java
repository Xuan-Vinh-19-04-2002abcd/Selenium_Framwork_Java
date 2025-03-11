package Configurations;

import Model.Config;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class ConfigReader {
    private static Config config;

    private ConfigReader() {}

    public static synchronized Config getConfiguration() {
        if (config == null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                // Đọc file config từ resources
                config = objectMapper.readValue(Paths.get("src/main/resources/config.json").toFile(), Config.class);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to load configuration");
            }
        }
        return config;
    }
}
