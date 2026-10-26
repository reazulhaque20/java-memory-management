package com.tutorial.memory.demos;

import com.tutorial.memory.util.MemoryPrinter;
import java.util.ArrayList;
import java.util.List;

public final class HeapStackDemo {

    private HeapStackDemo() {
    }

    public static void run() {
        System.out.println("=== Heap vs Stack Demo ===");
        heapDemo();
        stackDemo();
    }

    private static void heapDemo() {
        System.out.println("\nHeap demo: allocating 1MB blocks until memory pressure appears");
        List<byte[]> blocks = new ArrayList<>();
        MemoryPrinter.printHeap("heap-start");

        try {
            for (int i = 1; i <= 10_000; i++) {
                blocks.add(new byte[1024 * 1024]);
                if (i % 16 == 0) {
                    MemoryPrinter.printHeap("heap-blocks=" + i);
                }
            }
        } catch (OutOfMemoryError error) {
            System.out.println("Expected heap pressure reached: " + error);
        }

        blocks.clear();
        System.gc();
        MemoryPrinter.printHeap("heap-after-clear");
    }

    private static void stackDemo() {
        System.out.println("\nStack demo: recursive call growth until StackOverflowError");
        try {
            recurse(1);
        } catch (StackOverflowError error) {
            System.out.println("Expected stack overflow reached: " + error);
        }
    }

    private static void recurse(int depth) {
        if (depth % 1_000 == 0) {
            System.out.println("Current depth: " + depth);
        }
        recurse(depth + 1);
    }
}
