package com.tutorial.memory.demos;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public final class OffHeapDirectBufferDemo {

    private OffHeapDirectBufferDemo() {
    }

    public static void run() {
        System.out.println("=== Off-Heap Direct Buffer Demo ===");
        System.out.println("Tip: run with -XX:MaxDirectMemorySize=64m for quick demo");

        List<ByteBuffer> buffers = new ArrayList<>();
        int mb = 0;

        try {
            while (true) {
                buffers.add(ByteBuffer.allocateDirect(4 * 1024 * 1024));
                mb += 4;
                if (mb % 16 == 0) {
                    System.out.println("Allocated direct memory ~" + mb + "MB");
                }
            }
        } catch (OutOfMemoryError error) {
            System.out.println("Expected direct-memory pressure reached: " + error);
        }

        buffers.clear();
        System.gc();

        System.out.println("Use direct buffers for high-throughput I/O (NIO/netty style workloads).");
        System.out.println("Avoid excessive direct allocations for small, short-lived data blocks.");
    }
}
