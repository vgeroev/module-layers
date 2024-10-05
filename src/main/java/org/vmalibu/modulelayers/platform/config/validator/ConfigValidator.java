package org.vmalibu.modulelayers.platform.config.validator;

import org.vmalibu.modulelayers.platform.config.ModuleLayersConfig;

public interface ConfigValidator {

    void validate(ModuleLayersConfig config);
}
