package org.vmalibu.modulelayers.platform.config.parser;

import org.vmalibu.modulelayers.platform.config.ModuleLayersConfig;
import org.vmalibu.modulelayers.platform.config.validator.ConfigValidator;

import java.io.IOException;
import java.nio.file.Path;

public abstract class ConfigParser {

    private final ConfigValidator validator;

    protected ConfigParser(ConfigValidator validator) {
        this.validator = validator;
    }

    public ModuleLayersConfig parse(Path configPath, Path layersPath) throws IOException {
        ModuleLayersConfig config = internalParse(configPath, layersPath);
        validator.validate(config);
        return config;
    }

    protected abstract ModuleLayersConfig internalParse(Path configPath, Path layersPath) throws IOException;
}
