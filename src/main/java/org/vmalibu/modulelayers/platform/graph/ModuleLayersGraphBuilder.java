package org.vmalibu.modulelayers.platform.graph;

import org.vmalibu.modulelayers.platform.config.ModuleLayerEntry;
import org.vmalibu.modulelayers.platform.config.ModuleLayersConfig;
import org.vmalibu.modulelayers.platform.factory.ModuleLayerFactory;

import java.util.*;

public class ModuleLayersGraphBuilder {

    private final ModuleLayerFactory moduleLayerFactory;

    public ModuleLayersGraphBuilder(ModuleLayerFactory moduleLayerFactory) {
        this.moduleLayerFactory = moduleLayerFactory;
    }

    public ModuleLayersGraph build(ModuleLayersConfig config) {
        ModuleLayersGraph graph = new ModuleLayersGraphImpl(moduleLayerFactory);

        List<ModuleLayerEntry> moduleLayerEntries = config.moduleLayerEntries();
        // TODO Validate that graph is acyclic
        for (ModuleLayerEntry entry : moduleLayerEntries) {
            graph.add(entry.id(), entry.jarPaths(), entry.parentIds(), entry.injectLifecycleHandler());
        }

        return graph;
    }

}
