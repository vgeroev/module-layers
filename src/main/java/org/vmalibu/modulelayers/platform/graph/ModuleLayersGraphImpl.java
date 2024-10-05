package org.vmalibu.modulelayers.platform.graph;

import org.vmalibu.modulelayers.platform.factory.ModuleLayerFactory;
import org.vmalibu.modulelayers.platform.layer.ModuleLayerInfo;
import org.vmalibu.modulelayers.platform.layer.ModuleLayerInfoImpl;

import java.nio.file.Path;
import java.util.*;

public class ModuleLayersGraphImpl implements ModuleLayersGraph {

    private final Map<String, ModuleLayerNode> registry = new HashMap<>();
    private final ModuleLayerFactory moduleLayerFactory;

    public ModuleLayersGraphImpl(ModuleLayerFactory moduleLayerFactory) {
        this.moduleLayerFactory = moduleLayerFactory;
    }

    @Override
    public ModuleLayerInfo findById(String id) {
        ModuleLayerNode node = registry.get(id);
        return node == null ? null : node.layer;
    }

    @Override
    public void add(String id, List<Path> jarPaths, Set<String> parentIds, boolean injectLifecycleHandler) {
        if (registry.containsKey(id)) {
            throw new IllegalArgumentException("layer already exists with id=" + id);
        }

        ModuleLayer moduleLayer = buildLayer(jarPaths, parentIds, injectLifecycleHandler);
        ModuleLayerInfo layer = new ModuleLayerInfoImpl(id, moduleLayer);

        ModuleLayerNode node = new ModuleLayerNode(layer, new ArrayList<>(parentIds.size()), new ArrayList<>());
        for (String parentId : parentIds) {
            ModuleLayerNode parentNode = validateAndGetNode(parentId);
            parentNode.childes.add(node);
            node.parents.add(parentNode);
        }

        registry.put(id, node);
    }

    @Override
    public void remove(String rootId) {
        ModuleLayerNode node = validateAndGetNode(rootId);
        for (ModuleLayerNode parent : node.parents) {
            parent.childes.remove(node);
        }

        Queue<String> queue = new LinkedList<>();
        queue.add(node.layer.getId());
        while (!queue.isEmpty()) {
            String nextId = queue.remove();
            ModuleLayerNode nextNode = registry.get(nextId);
            for (ModuleLayerNode child : nextNode.childes) {
                queue.add(child.layer.getId());
            }
            registry.remove(nextId);
        }
    }

    @Override
    public List<ModuleLayerInfo> getAll() {
        return registry.values()
                .stream()
                .map(ModuleLayerNode::layer)
                .toList();
    }

    private ModuleLayerNode validateAndGetNode(String id) {
        ModuleLayerNode node = registry.get(id);
        if (node == null) {
            throw new IllegalArgumentException("There is no layer with id=" + id);
        }
        return node;
    }

    private ModuleLayer buildLayer(List<Path> jarPaths, Set<String> parentIds, boolean injectLifecycleHandler) {
        List<ModuleLayer> parentLayers = new ArrayList<>(parentIds.size());
        for (String parentId : parentIds) {
            ModuleLayerInfo parent = registry.get(parentId).layer;
            parentLayers.add(parent.getModuleLayer());
        }

        if (injectLifecycleHandler) {
            return moduleLayerFactory.buildLifecycleHandler(jarPaths, parentLayers);
        } else {
            return moduleLayerFactory.build(jarPaths, parentLayers);
        }
    }

    private record ModuleLayerNode(ModuleLayerInfo layer, List<ModuleLayerNode> parents, List<ModuleLayerNode> childes) { }

}
