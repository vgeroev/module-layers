package org.vmalibu.modulelayers.platform.config;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public record ModuleLayerEntry(String id, List<Path> jarPaths, Set<String> parentIds, boolean injectLifecycleHandler) {
}
