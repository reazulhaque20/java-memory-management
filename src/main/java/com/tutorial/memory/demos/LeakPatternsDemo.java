package com.tutorial.memory.demos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LeakPatternsDemo {

    private static final Map<String, byte[]> STATIC_CACHE = new HashMap<>();
    private static final List<Runnable> LISTENERS = new ArrayList<>();

    private LeakPatternsDemo() {
    }

    public static void run() {
        System.out.println("=== Common Java Leak Patterns Demo ===");
        staticCacheLeakPattern();
        listenerLeakPattern();
        fixPattern();
    }

    private static void staticCacheLeakPattern() {
        for (int i = 0; i < 2_000; i++) {
            STATIC_CACHE.put("key-" + i, new byte[32 * 1024]);
        }
        System.out.println("Static cache size (growing): " + STATIC_CACHE.size());
        System.out.println("Risk: unbounded static maps can retain memory forever.");
    }

    private static void listenerLeakPattern() {
        for (int i = 0; i < 10_000; i++) {
            LISTENERS.add(() -> {
                // Intentionally empty; simulates leaked callbacks/subscribers.
            });
        }
        System.out.println("Listener registrations: " + LISTENERS.size());
        System.out.println("Risk: registering without unregistering causes retention leaks.");
    }

    private static void fixPattern() {
        STATIC_CACHE.clear();
        LISTENERS.clear();
        System.gc();

        System.out.println("Applied cleanup: cleared caches/listeners.");
        System.out.println("Real fix: use bounded caches, weak listeners, and lifecycle cleanup hooks.");
    }
}
