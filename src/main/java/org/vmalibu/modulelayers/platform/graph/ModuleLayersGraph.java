package org.vmalibu.modulelayers.platform.graph;

import org.vmalibu.modulelayers.platform.layer.ModuleLayerInfo;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public interface ModuleLayersGraph {

    ModuleLayerInfo findById(String id);

    void add(String id, List<Path> jarPaths, Set<String> parentIds, boolean injectLifecycleHandler);

    void remove(String rootId);

    List<ModuleLayerInfo> getAll();

}
