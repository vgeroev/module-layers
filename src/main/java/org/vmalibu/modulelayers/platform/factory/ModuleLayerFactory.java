package org.vmalibu.modulelayers.platform.factory;

import java.nio.file.Path;
import java.util.List;

public interface ModuleLayerFactory {

    ModuleLayer build(List<Path> modulePaths, List<ModuleLayer> parentLayers);

    ModuleLayer buildLifecycleHandler(List<Path> modulePaths, List<ModuleLayer> parentLayers);
}
