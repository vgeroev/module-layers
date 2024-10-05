package org.vmalibu.modulelayers.platform.config.parser;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.vmalibu.modulelayers.platform.config.AppMainConfig;
import org.vmalibu.modulelayers.platform.config.ModuleLayerEntry;
import org.vmalibu.modulelayers.platform.config.ModuleLayersConfig;
import org.vmalibu.modulelayers.platform.config.validator.ConfigValidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class JsonConfigParser extends ConfigParser {

    private static final String JSON_MAIN = "main";
    private static final String JSON_MAIN_CLASS = "main_class";
    private static final String JSON_MAIN_MODULE_NAME = "module_name";
    private static final String JSON_MAIN_LAYER_ID = "layer_id";
    private static final String JSON_LAYERS = "layers";
    private static final String JSON_LAYER_ID = "id";
    private static final String JSON_LAYER_INJECT_LIFECYCLE_HANDLER = "inject_lifecycle_handler";
    private static final String JSON_LAYER_PARENT_IDS = "parent_ids";

    public JsonConfigParser(ConfigValidator validator) {
        super(validator);
    }

    @Override
    protected ModuleLayersConfig internalParse(Path configPath, Path layersPath) throws IOException {
        JSONObject json = loadJson(configPath);
        AppMainConfig appMainConfig = parseAppMainConfig(json);
        List<ModuleLayerEntry> moduleLayerEntries = parseLayers(json, layersPath);

        return new ModuleLayersConfig(appMainConfig, moduleLayerEntries);
    }

    private List<ModuleLayerEntry> parseLayers(JSONObject json, Path layersPath) throws IOException {
        if (!json.has(JSON_LAYERS)) {
            throw new IllegalArgumentException("There is no `layers` block");
        }

        JSONArray jLayers = json.getJSONArray(JSON_LAYERS);
        List<ModuleLayerEntry> moduleLayerEntries = new ArrayList<>();
        for (Object oLayer : jLayers) {
            JSONObject layer = (JSONObject) oLayer;
            moduleLayerEntries.add(parseLayer(layer, layersPath));
        }

        return moduleLayerEntries;
    }

    private static ModuleLayerEntry parseLayer(JSONObject layer, Path layersPath) throws IOException {
        String id = layer.getString(JSON_LAYER_ID);
        boolean injectLifecycleHandler;
        if (layer.has(JSON_LAYER_INJECT_LIFECYCLE_HANDLER)) {
            injectLifecycleHandler = layer.getBoolean(JSON_LAYER_INJECT_LIFECYCLE_HANDLER);
        } else {
            injectLifecycleHandler = false;
        }

        Set<String> parentIds = new HashSet<>();
        JSONArray jParentIds = (JSONArray) layer.get(JSON_LAYER_PARENT_IDS);
        for (Object parentId : jParentIds) {
            parentIds.add((String) parentId);
        }

        List<Path> jarPaths;
        Path dirJars = layersPath.resolve(id);
        try (Stream<Path> fileStream = Files.list(dirJars)) {
            jarPaths = fileStream.toList();
        }

        return new ModuleLayerEntry(id, jarPaths, parentIds, injectLifecycleHandler);
    }

    private static AppMainConfig parseAppMainConfig(JSONObject json) {
        if (!json.has(JSON_MAIN)) {
            throw new IllegalArgumentException("Json config does not have main block");
        }

        JSONObject main = (JSONObject) json.get(JSON_MAIN);
        String layerId = main.getString(JSON_MAIN_LAYER_ID);
        String moduleName = main.getString(JSON_MAIN_MODULE_NAME);
        String mainClass = main.getString(JSON_MAIN_CLASS);

        return new AppMainConfig(layerId, moduleName, mainClass);
    }

    private static JSONObject loadJson(Path configPath) throws IOException {
        String configString = Files.readString(configPath);
        JSONTokener jsonTokener = new JSONTokener(configString);
        return new JSONObject(jsonTokener);
    }
}
