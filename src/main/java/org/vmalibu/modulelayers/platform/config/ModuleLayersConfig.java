package org.vmalibu.modulelayers.platform.config;

import java.util.List;

public record ModuleLayersConfig(AppMainConfig appMainConfig, List<ModuleLayerEntry> moduleLayerEntries) {

}
