package org.vmalibu.modulelayers.platform.layer;


public class ModuleLayerInfoImpl implements ModuleLayerInfo {

    private final String id;
    private final ModuleLayer moduleLayer;

    public ModuleLayerInfoImpl(String id, ModuleLayer moduleLayer) {
        this.id = id;
        this.moduleLayer = moduleLayer;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ModuleLayer getModuleLayer() {
        return moduleLayer;
    }

}
