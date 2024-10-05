package org.vmalibu.m.one;

import com.infomaximum.modulelayers.api.ModuleLayersServiceHolder;

import java.io.IOException;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("App main module start");
        System.out.println("---Run services on start---");
        runServices();
        System.out.println("-------------------------------");
        ModuleLayersServiceHolder.get().addLayer("m-1-2", Set.of("m-1"));
        runServices();
        System.out.println("-------------------------------");
        ModuleLayersServiceHolder.get().removeLayer("m-1-2");
        runServices();
        System.out.println("-------------------------------");
        ModuleLayersServiceHolder.get().addLayer("m-1-2", Set.of("m-1"));
        runServices();
        System.out.println("-------------------------------");
        ModuleLayersServiceHolder.get().removeLayer("m-1-1");
        runServices();
        System.out.println("---Running new version of m-1-1---");
        ModuleLayersServiceHolder.get().addLayer("m-1-1-modificated", Set.of("m-1"));
        runServices();
    }

    private static void runServices() {
        List<ModuleLayer> moduleLayers = ModuleLayersServiceHolder.get().getModuleLayers();
        for (ModuleLayer moduleLayer : moduleLayers) {
            ServiceLoader<ServiceRunner> services = ServiceLoader.load(moduleLayer, ServiceRunner.class);
            for (ServiceRunner service : services) {
                service.run();
            }
        }
    }
}
