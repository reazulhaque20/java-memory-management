package com.tutorial.memory.demos;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class ThreadLocalLeakDemo {

    private static final ThreadLocal<byte[]> CONTEXT = new ThreadLocal<>();

    private ThreadLocalLeakDemo() {
    }

    public static void run() {
        System.out.println("=== ThreadLocal Leak Demo ===");

        ExecutorService pool = Executors.newFixedThreadPool(1);

        try {
            pool.submit(() -> {
                CONTEXT.set(new byte[20 * 1024 * 1024]);
                System.out.println("Task-1 set ThreadLocal value on pooled thread");
                // Deliberately no remove() to demonstrate stale data/leak pattern.
            }).get();

            pool.submit(() -> {
                byte[] existing = CONTEXT.get();
                System.out.println("Task-2 sees old value still present? " + (existing != null));
                CONTEXT.remove();
                System.out.println("Task-2 removed ThreadLocal value");
            }).get();

            pool.submit(() -> {
                System.out.println("Task-3 value present after remove? " + (CONTEXT.get() != null));
            }).get();
        } catch (Exception exception) {
            System.out.println("Demo failure: " + exception.getMessage());
        } finally {
            pool.shutdown();
            try {
                pool.awaitTermination(2, TimeUnit.SECONDS);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("When to use ThreadLocal: thread-confined context data.");
        System.out.println("In thread pools, always call remove() in finally blocks.");
    }
}
