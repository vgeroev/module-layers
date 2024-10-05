package org.vmalibu.modulelayers.platform.config.validator;

import org.vmalibu.modulelayers.platform.config.AppMainConfig;
import org.vmalibu.modulelayers.platform.config.ModuleLayerEntry;
import org.vmalibu.modulelayers.platform.config.ModuleLayersConfig;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigValidatorImpl implements ConfigValidator {

    @Override
    public void validate(ModuleLayersConfig config) {
        AppMainConfig appMainConfig = config.appMainConfig();
        List<ModuleLayerEntry> moduleLayerEntries = config.moduleLayerEntries();

        validateAppMainLayerExistence(appMainConfig.layerId(), moduleLayerEntries);
        validateLayerIdsUniqueness(moduleLayerEntries);
        validateParentLayersExistence(moduleLayerEntries);
        validateModuleFilesExistence(moduleLayerEntries);
    }

    private static void validateAppMainLayerExistence(String mainLayerId, List<ModuleLayerEntry> moduleLayerEntries) {
        boolean exists = false;
        for (ModuleLayerEntry entry : moduleLayerEntries) {
            if (mainLayerId.equals(entry.id())) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            throw new IllegalStateException("Main layer does not exists");
        }
    }

    private static void validateLayerIdsUniqueness(List<ModuleLayerEntry> moduleLayerEntries) {
        Set<String> ids = new HashSet<>();
        for (ModuleLayerEntry entry : moduleLayerEntries) {
            String id = entry.id();
            if (ids.contains(id)) {
                throw new IllegalArgumentException("Config is not valid: Layer identifiers duplication (id: %s)".formatted(id));
            }
            ids.add(id);
        }
    }

    private static void validateParentLayersExistence(List<ModuleLayerEntry> moduleLayerEntries) {
        Set<String> ids = moduleLayerEntries.stream().map(ModuleLayerEntry::id).collect(Collectors.toSet());
        Set<String> parentIds = moduleLayerEntries.stream().flatMap(x -> x.parentIds().stream()).collect(Collectors.toSet());

        for (String parentId : parentIds) {
            if (!ids.contains(parentId)) {
                throw new IllegalArgumentException("Config is not valid: There is no layer with id = " + parentId);
            }
        }
    }

    private static void validateModuleFilesExistence(List<ModuleLayerEntry> moduleLayerEntries) {
        for (ModuleLayerEntry entry : moduleLayerEntries) {
            List<Path> modulePaths = entry.jarPaths();
            for (Path modulePath : modulePaths) {
                if (!Files.exists(modulePath)) {
                    throw new IllegalArgumentException("Config is not valid: There is no file: " + modulePath);
                }
            }
        }
    }
}
