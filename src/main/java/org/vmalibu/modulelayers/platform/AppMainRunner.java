package org.vmalibu.modulelayers.platform;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppMainRunner {

//    private static final Logger log = LoggerFactory.getLogger(AppMainRunner.class);

    private static ExecutorService appMainThread;
    private static CompletableFuture<Object> terminationHook;

    private AppMainRunner() { }

    public static synchronized void run(ModuleLayer mainLayer, String moduleName, String mainClass, String[] args) {
        if (terminationHook != null && !terminationHook.isDone()) {
            throw new IllegalStateException("App main not terminated yet");
        }

        Method mainMethod;
        try {
            ClassLoader loader = mainLayer.findLoader(moduleName);
            Class<?> mainClazz = loader.loadClass(mainClass);
            mainMethod = mainClazz.getDeclaredMethod("main", String[].class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }

        appMainThread = Executors.newSingleThreadExecutor();
        terminationHook = CompletableFuture.supplyAsync(() -> {
                try {
                    mainMethod.invoke(null, (Object) args);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new IllegalStateException(e);
                }
                return null;
            }, appMainThread
        ).whenComplete((aVoid, throwable) -> {
            if (throwable != null) {
//                log.error("App main error", throwable);
                System.out.println(throwable);
            }
            appMainThread.shutdown();
        });
    }

    public static synchronized void awaitTermination() {
        try {
            if (terminationHook != null) {
                terminationHook.get();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        } catch (ExecutionException e) {
            throw new IllegalStateException(e);
        } finally {
            if (appMainThread != null) {
                appMainThread.shutdown();
            }
        }
    }

}
