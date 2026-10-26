package com.tutorial.memory.demos;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public final class MetaspaceDemo {

    private MetaspaceDemo() {
    }

    public static void run() {
        System.out.println("=== Metaspace and ClassLoader Demo ===");
        System.out.println("Tip: run with -XX:MaxMetaspaceSize=96m for fast pressure");

        URL codeSource = SampleLoadedClass.class.getProtectionDomain().getCodeSource().getLocation();
        List<ClassLoader> leakedLoaders = new ArrayList<>();

        try {
            for (int i = 1; i <= 100_000; i++) {
                URLClassLoader loader = new URLClassLoader(new URL[]{codeSource}, ClassLoader.getPlatformClassLoader());
                Class.forName("com.tutorial.memory.demos.SampleLoadedClass", true, loader);

                // Intentionally keep references so class metadata cannot be reclaimed.
                leakedLoaders.add(loader);

                if (i % 500 == 0) {
                    System.out.println("Created class loaders: " + i);
                }
            }
        } catch (OutOfMemoryError error) {
            System.out.println("Expected metaspace pressure reached: " + error);
        } catch (Exception exception) {
            System.out.println("Demo ended with exception: " + exception.getMessage());
        }

        System.out.println("When to use: plugin systems and module isolation.");
        System.out.println("Critical rule: release class loader references on undeploy/reload.");
    }
}
