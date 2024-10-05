package org.vmalibu.modulelayers.platform;

import com.infomaximum.modulelayers.api.initializer.ModuleLayersServiceInitializer;
import org.vmalibu.modulelayers.platform.config.AppMainConfig;
import org.vmalibu.modulelayers.platform.config.ModuleLayersConfig;
import org.vmalibu.modulelayers.platform.config.parser.ConfigParser;
import org.vmalibu.modulelayers.platform.config.parser.JsonConfigParser;
import org.vmalibu.modulelayers.platform.config.validator.ConfigValidatorImpl;
import org.vmalibu.modulelayers.platform.factory.ModuleLayerFactoryImpl;
import org.vmalibu.modulelayers.platform.graph.ModuleLayersGraph;
import org.vmalibu.modulelayers.platform.graph.ModuleLayersGraphBuilder;
import org.vmalibu.modulelayers.platform.layer.ModuleLayerInfo;
import org.vmalibu.modulelayers.platform.lifecycle.ModuleLayersServiceImpl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class Main {

    public static void main(String[] args) throws IOException {
        Path currentDir = Path.of("").toAbsolutePath();
        Path configPath = currentDir.resolve("config/config.json");
        Path layersPath = currentDir.resolve("config/layers");

        ConfigParser configParser = getConfigParser();
        ModuleLayersConfig config = configParser.parse(configPath, layersPath);

        ModuleLayersGraph graph = new ModuleLayersGraphBuilder(new ModuleLayerFactoryImpl()).build(config);
        ModuleLayersServiceInitializer.init(new ModuleLayersServiceImpl(graph, configPath, layersPath, configParser));

        runAppMain(config.appMainConfig(), graph, args);
    }

    private static ConfigParser getConfigParser() {
        return new JsonConfigParser(new ConfigValidatorImpl());
    }

    private static void runAppMain(AppMainConfig config, ModuleLayersGraph graph, String[] args) {
        String mainLayerId = config.layerId();
        ModuleLayerInfo mainLayer = Objects.requireNonNull(graph.findById(mainLayerId));
        AppMainRunner.run(mainLayer.getModuleLayer(), config.moduleName(), config.mainClass(), args);
    }

}
