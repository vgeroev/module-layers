package org.vmalibu.modulelayers.platform.lifecycle;

import com.infomaximum.modulelayers.api.ModuleLayersService;
import org.vmalibu.modulelayers.platform.config.parser.ConfigParser;
import org.vmalibu.modulelayers.platform.graph.ModuleLayersGraph;
import org.vmalibu.modulelayers.platform.layer.ModuleLayerInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ModuleLayersServiceImpl implements ModuleLayersService {

    private ModuleLayersGraph graph;
    private Path configPath;
    private Path layersPath;
    private final ConfigParser configParser;

    public ModuleLayersServiceImpl(ModuleLayersGraph graph,
                                   Path configPath,
                                   Path layersPath,
                                   ConfigParser configParser) {
        this.graph = graph;
        this.configPath = configPath;
        this.layersPath = layersPath;
        this.configParser = configParser;
    }

    @Override
    public void addLayer(String id, Set<String> parentIds) throws IOException {
        execWithIOException(() -> {
            System.out.println("Adding layer: " + id);

            List<Path> jarPaths;
            Path dirJars = layersPath.resolve(id);
            try (Stream<Path> fileStream = Files.list(dirJars)) {
                jarPaths = fileStream.toList();
            }

            graph.add(id, jarPaths, parentIds, false);
            return null;
        });
    }

    @Override
    public void removeLayer(String layerId) {
        System.out.println("Removing layer: " + layerId);
        graph.remove(layerId);
    }

    @Override
    public void restart(Path configPath, Path layersPath, Runnable appStopper) throws IOException {
//        execWithIOException(() -> {
//            ModuleLayersConfig config = configParser.parse(configPath, layersPath);
//            appStopper.run();
//            AppMainRunner.awaitTermination();
//
//            graph = new HotLayersGraphBuilder(new ModuleLayerFactoryImpl()).build(config);
//            AppMainConfig appMainConfig = config.appMainConfig();
//            HotLayer mainLayer = graph.findById(appMainConfig.layerId());
//
//            this.configPath = configPath;
//            this.layersPath = layersPath;
//            AppMainRunner.run(mainLayer.getModuleLayer(), appMainConfig.moduleName(), appMainConfig.mainClass(), new String[0]);
//            return null;
//        });
    }

    @Override
    public List<ModuleLayer> getModuleLayers() {
        return exec(() -> graph.getAll().stream()
                .map(ModuleLayerInfo::getModuleLayer)
                .toList());
    }

    private <T> T exec(Supplier<T> exec) {
        synchronized (ModuleLayersService.class) {
            return exec.get();
        }
    }

    private <T> T execWithIOException(SupplierIO<T> exec) throws IOException {
        synchronized (ModuleLayersService.class) {
            return exec.get();
        }
    }

    @FunctionalInterface
    private interface SupplierIO<T> {

        T get() throws IOException;
    }
}
