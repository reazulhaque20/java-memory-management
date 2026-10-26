package com.tutorial.memory.util;

public final class MemoryPrinter {

    private MemoryPrinter() {
    }

    public static void printHeap(String label) {
        Runtime runtime = Runtime.getRuntime();
        long maxMb = runtime.maxMemory() / (1024 * 1024);
        long totalMb = runtime.totalMemory() / (1024 * 1024);
        long freeMb = runtime.freeMemory() / (1024 * 1024);
        long usedMb = totalMb - freeMb;
        System.out.printf("[%s] Heap used=%dMB, free=%dMB, total=%dMB, max=%dMB%n",
                label, usedMb, freeMb, totalMb, maxMb);
    }
}
