package com.tutorial.memory.demos;

import com.tutorial.memory.util.MemoryPrinter;
import java.util.ArrayList;
import java.util.List;

public final class GarbageCollectionDemo {

    private GarbageCollectionDemo() {
    }

    public static void run() {
        System.out.println("=== Garbage Collection Demo ===");
        System.out.println("Tip: run with -Xlog:gc* or -verbose:gc to watch collector behavior");

        MemoryPrinter.printHeap("gc-start");

        for (int round = 1; round <= 5; round++) {
            List<byte[]> garbage = new ArrayList<>();
            for (int i = 0; i < 80; i++) {
                garbage.add(new byte[1024 * 1024]);
            }
            MemoryPrinter.printHeap("gc-round=" + round + " before-clear");
            garbage = null;

            System.gc();
            MemoryPrinter.printHeap("gc-round=" + round + " after-gc");
        }

        System.out.println("\nWhen to use explicit System.gc(): usually avoid it in production.");
        System.out.println("Use it only for controlled experiments, not regular request paths.");
    }
}
