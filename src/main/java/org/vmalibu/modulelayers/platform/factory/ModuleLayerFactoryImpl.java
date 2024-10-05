package org.vmalibu.modulelayers.platform.factory;

import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class ModuleLayerFactoryImpl implements ModuleLayerFactory {

    @Override
    public ModuleLayer build(List<Path> modulePaths, List<ModuleLayer> parentLayers) {
        List<ModuleLayer> newParentLayers = new ArrayList<>(parentLayers);
        if (newParentLayers.isEmpty()) {
            newParentLayers.add(ModuleLayer.boot());
        }
        return internalBuild(modulePaths, newParentLayers);
    }

    @Override
    public ModuleLayer buildLifecycleHandler(List<Path> modulePaths, List<ModuleLayer> parentLayers) {
        List<ModuleLayer> newParentLayers = new ArrayList<>(parentLayers);
        newParentLayers.add(getCurrentModuleLayer());
        return internalBuild(modulePaths, newParentLayers);
    }

    private ModuleLayer getCurrentModuleLayer() {
        return this.getClass().getModule().getLayer();
    }

    private ModuleLayer internalBuild(List<Path> modulePaths, List<ModuleLayer> parentLayers) {
        ClassLoader scl = ClassLoader.getSystemClassLoader();

        ModuleFinder finder = ModuleFinder.of(modulePaths.toArray(Path[]::new));

        Set<String> roots = finder.findAll()
                .stream()
                .map(m -> m.descriptor().name())
                .collect(Collectors.toSet());

        Configuration configuration = Configuration.resolve(
                finder,
                parentLayers.stream().map(ModuleLayer::configuration).toList(),
                ModuleFinder.of(),
                roots
        );

        return ModuleLayer.defineModulesWithOneLoader(configuration, parentLayers, scl).layer();
    }
}
