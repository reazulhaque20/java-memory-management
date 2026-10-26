package com.tutorial.memory;

import com.tutorial.memory.demos.GarbageCollectionDemo;
import com.tutorial.memory.demos.HeapStackDemo;
import com.tutorial.memory.demos.JfrProfilingDemo;
import com.tutorial.memory.demos.LeakPatternsDemo;
import com.tutorial.memory.demos.MetaspaceDemo;
import com.tutorial.memory.demos.OffHeapDirectBufferDemo;
import com.tutorial.memory.demos.ReferencesDemo;
import com.tutorial.memory.demos.ThreadLocalLeakDemo;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        Map<String, Runnable> demos = new LinkedHashMap<>();
        demos.put("heap-stack", HeapStackDemo::run);
        demos.put("gc", GarbageCollectionDemo::run);
        demos.put("references", ReferencesDemo::run);
        demos.put("offheap", OffHeapDirectBufferDemo::run);
        demos.put("metaspace", MetaspaceDemo::run);
        demos.put("threadlocal", ThreadLocalLeakDemo::run);
        demos.put("leaks", LeakPatternsDemo::run);
        demos.put("jfr", JfrProfilingDemo::run);

        if (args.length == 0) {
            printUsage(demos);
            return;
        }

        String command = args[0].trim().toLowerCase();
        Runnable demo = demos.get(command);
        if (demo == null) {
            System.out.println("Unknown demo: " + command);
            printUsage(demos);
            return;
        }

        demo.run();
    }

    private static void printUsage(Map<String, Runnable> demos) {
        System.out.println("Java Memory Management Tutorial");
        System.out.println("Usage: mvn -q exec:java -Dexec.args=\"<demo>\"");
        System.out.println();
        System.out.println("Available demos:");
        for (String key : demos.keySet()) {
            System.out.println("- " + key);
        }
    }
}
