package com.tutorial.memory.demos;

import com.tutorial.memory.util.MemoryPrinter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class GcComparisonWorkload {

    private GcComparisonWorkload() {
    }

    public static void main(String[] args) {
        int seconds = 20;
        if (args.length > 0) {
            try {
                seconds = Math.max(5, Integer.parseInt(args[0]));
            } catch (NumberFormatException ignored) {
                seconds = 20;
            }
        }

        System.out.println("GC comparison workload started for " + seconds + " seconds");
        long before = System.nanoTime();
        runWorkload(Duration.ofSeconds(seconds));
        long elapsedMs = (System.nanoTime() - before) / 1_000_000;
        System.out.println("GC comparison workload finished in " + elapsedMs + " ms");
    }

    public static void runWorkload(Duration duration) {
        long endNanos = System.nanoTime() + duration.toNanos();

        List<byte[]> longLived = new ArrayList<>();
        long allocations = 0;
        int cycle = 0;

        while (System.nanoTime() < endNanos) {
            List<byte[]> burst = new ArrayList<>();
            for (int i = 0; i < 800; i++) {
                int size = ThreadLocalRandom.current().nextInt(8 * 1024, 128 * 1024);
                burst.add(new byte[size]);
                allocations++;
            }

            for (int i = 0; i < 24; i++) {
                longLived.add(new byte[64 * 1024]);
                allocations++;
            }

            if (longLived.size() > 4_000) {
                longLived = new ArrayList<>(longLived.subList(2_000, longLived.size()));
            }

            cycle++;
            if (cycle % 2000 == 0) {
                MemoryPrinter.printHeap("gc-workload-cycle=" + cycle + ", allocations=" + allocations);
            }
        }

        System.out.println("Total allocations created: " + allocations);
    }
}
