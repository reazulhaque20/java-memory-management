# Java Memory Management - Complete Tutorial Project

This project is a practical, runnable guide to JVM memory management for learning and teaching.

## What you get

- Heap vs Stack behavior
- Garbage Collection basics and tuning flags
- Java references (Strong, Soft, Weak, Phantom)
- Off-heap memory with direct buffers
- Metaspace and class loader leaks
- ThreadLocal memory leak patterns
- Common memory leak patterns in Java apps
- Java Flight Recorder (JFR) profiling demo
- GC comparison benchmark (G1 vs ZGC)
- When to use each feature and when not to

## Prerequisites

- JDK 21+
- Maven 3.9+

## Build

```bash
mvn clean compile
```

## Run all available demos list

```bash
mvn -q exec:java
```

## Run a specific demo

```bash
mvn -q exec:java -Dexec.args="heap-stack"
mvn -q exec:java -Dexec.args="gc"
mvn -q exec:java -Dexec.args="references"
mvn -q exec:java -Dexec.args="offheap"
mvn -q exec:java -Dexec.args="metaspace"
mvn -q exec:java -Dexec.args="threadlocal"
mvn -q exec:java -Dexec.args="leaks"
mvn -q exec:java -Dexec.args="jfr"
```

## Recommended JVM flags for learning

Use small limits to see behavior faster:

```bash
mvn -q exec:java -Dexec.args="heap-stack" -Dexec.jvmArgs="-Xms128m -Xmx128m"
mvn -q exec:java -Dexec.args="offheap" -Dexec.jvmArgs="-XX:MaxDirectMemorySize=64m"
mvn -q exec:java -Dexec.args="metaspace" -Dexec.jvmArgs="-XX:MaxMetaspaceSize=96m"
```

## Feature guide: how to use and when to use

| Feature | How to use | When to use | Avoid when |
|---|---|---|---|
| Heap sizing (`-Xms`, `-Xmx`) | Set startup and max heap | You need predictable memory behavior in production | You guess values without profiling |
| GC selection (`-XX:+UseG1GC`, ZGC, Shenandoah) | Choose collector by pause/throughput goals | Latency-sensitive or large-heap apps | Blindly using defaults for all workloads |
| SoftReference | Cache memory-sensitive data | Re-creatable cached data | Core business state |
| WeakReference / WeakHashMap | Canonical maps, listener refs, metadata | Keys should vanish when unreferenced | Objects must stay strongly alive |
| PhantomReference + ReferenceQueue | Post-mortem cleanup tracking | Advanced resource lifecycle management | Simpler cleaner/finally alternatives are enough |
| DirectByteBuffer (off-heap) | `ByteBuffer.allocateDirect(...)` | High-throughput I/O and reduced copy operations | Small apps with little I/O pressure |
| ThreadLocal | Per-thread context (request IDs, formatters) | Request-scoped data in thread-confined execution | You forget `remove()` in thread pools |
| Class loaders / metaspace | Isolate modules/plugins | Plugin architectures, hot-reload systems | You keep class loaders strongly referenced |
| JFR (Java Flight Recorder) | Start recording and analyze .jfr in JMC | Production-safe profiling with low overhead | You rely only on logs for performance root cause |

## Suggested learning order

1. `heap-stack`
2. `gc`
3. `references`
4. `offheap`
5. `threadlocal`
6. `leaks`
7. `metaspace`
8. `jfr`

## Java Flight Recorder walkthrough

Run the JFR demo:

```bash
mvn -q exec:java -Dexec.args="jfr"
```

What it does:

- Starts a JFR recording programmatically
- Runs an allocation-heavy workload for ~12 seconds
- Writes recording to `target/jfr/memory-demo.jfr`

Inspect quickly from terminal:

```bash
jfr summary target/jfr/memory-demo.jfr
jfr print --events GCPhasePause target/jfr/memory-demo.jfr | head -n 40
```

Deep analysis in Java Mission Control (JMC):

1. Open JMC.
2. Open file `target/jfr/memory-demo.jfr`.
3. Inspect tabs: Memory, Garbage Collections, Allocation, Threads.
4. Compare pause behavior with GC logs from the GC comparison script below.

When to use JFR:

- Investigating memory churn, allocation hotspots, and GC pauses
- Production diagnostics (typically low overhead)

## GC comparison script: G1 vs ZGC

Run script (same workload for both collectors):

```bash
chmod +x scripts/compare-gc.sh
./scripts/compare-gc.sh 20
```

Output files:

- `target/gc-compare/g1.log`
- `target/gc-compare/zgc.log`
- `target/gc-compare/g1.time`
- `target/gc-compare/zgc.time`
- `target/gc-compare/summary.txt`

The script compares:

- Wall-clock runtime (`time -p`)
- Number of GC log events

How to interpret:

- Lower pauses and stable latency are often where ZGC shines.
- Throughput with moderate heaps can be very strong with G1.
- Final choice should be based on your real workload and SLOs, not synthetic results alone.

## Useful diagnostics commands

```bash
jcmd <pid> GC.heap_info
jcmd <pid> VM.native_memory summary
jcmd <pid> GC.class_histogram
jmap -histo:live <pid>
jstat -gcutil <pid> 1000
```

## Notes

- Some demos intentionally trigger memory pressure or `OutOfMemoryError`.
- Run one demo at a time.
- For safer runs, reduce loop counts in code.


Test update for rebase
