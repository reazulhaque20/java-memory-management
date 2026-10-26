package com.tutorial.memory.demos;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

public final class ReferencesDemo {

    private ReferencesDemo() {
    }

    public static void run() {
        System.out.println("=== Java Reference Types Demo ===");
        strongAndWeak();
        softReferenceExample();
        phantomReferenceExample();
    }

    private static void strongAndWeak() {
        Object strong = new Object();
        WeakReference<Object> weak = new WeakReference<>(strong);

        System.out.println("Strong + Weak present: weak.get() != null -> " + (weak.get() != null));
        strong = null;
        System.gc();

        System.out.println("After clearing strong reference and GC: weak.get() -> " + weak.get());
        System.out.println("Use WeakReference when object lifetime should not be prolonged.");
    }

    private static void softReferenceExample() {
        SoftReference<byte[]> soft = new SoftReference<>(new byte[10 * 1024 * 1024]);
        System.out.println("Soft reference created: " + (soft.get() != null));

        System.gc();
        System.out.println("After GC, soft may remain if memory is sufficient: " + (soft.get() != null));
        System.out.println("Use SoftReference for memory-sensitive cache values.");
    }

    private static void phantomReferenceExample() {
        ReferenceQueue<Object> queue = new ReferenceQueue<>();
        Object value = new Object();
        PhantomReference<Object> phantom = new PhantomReference<>(value, queue);

        System.out.println("Phantom get() is always null: " + phantom.get());
        value = null;
        System.gc();

        Reference<?> dequeued = queue.poll();
        System.out.println("Is object enqueued for post-mortem cleanup? " + (dequeued != null));
        System.out.println("Use PhantomReference with ReferenceQueue for advanced cleanup tracking.");
    }
}
