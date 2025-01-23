package dev.itznxthaniel.autoVMessages.handlers;

import dev.itznxthaniel.autoVMessages.AutoVMessages;
import lombok.Getter;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Objects;

@Getter
public class ConfigHandler {
    private ConfigurationNode config;

    public ConfigHandler(AutoVMessages plugin) {
        this.loadConfig(plugin);
    }

    public void loadConfig(AutoVMessages plugin) {
        File configFile = new File(plugin.getDataDirectory().toFile(), "config.yml");

        if (!configFile.exists()) {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml")) {
                Files.copy(Objects.requireNonNull(in), configFile.toPath());
            } catch (IOException exception) {
                plugin.getLogger().error("Failed to load config.yml", exception);
            }
        }

        YamlConfigurationLoader yamlConfigurationLoader = YamlConfigurationLoader.builder().file(configFile).build();

        try {
            config = yamlConfigurationLoader.load();
            if (config.node("debug").getBoolean()) AutoVMessages.DEBUG_ENABLED = true;
        } catch (IOException exception) {
            plugin.getLogger().error("Failed to load config.yml", exception);
        }
    }

}
