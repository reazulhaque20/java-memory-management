package com.tutorial.memory.demos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import jdk.jfr.Recording;

public final class JfrProfilingDemo {

    private JfrProfilingDemo() {
    }

    public static void run() {
        System.out.println("=== Java Flight Recorder (JFR) Demo ===");
        System.out.println("This demo starts a recording, runs allocation-heavy workload, and writes a .jfr file.");

        Path outputDir = Path.of("target", "jfr");
        Path recordingFile = outputDir.resolve("memory-demo.jfr");

        try {
            Files.createDirectories(outputDir);
        } catch (IOException e) {
            System.out.println("Failed to create output directory: " + e.getMessage());
            return;
        }

        Instant start = Instant.now();

        try (Recording recording = new Recording()) {
            recording.setName("memory-tutorial-recording");
            recording.setToDisk(true);
            recording.start();

            GcComparisonWorkload.runWorkload(Duration.ofSeconds(12));

            recording.stop();
            recording.dump(recordingFile);
            recording.close();

            Duration elapsed = Duration.between(start, Instant.now());
            System.out.println("JFR recording saved to: " + recordingFile.toAbsolutePath());
            System.out.println("Elapsed time: " + elapsed.toMillis() + " ms");
            System.out.println("Open file in Java Mission Control (JMC) for deep analysis.");
        } catch (Exception e) {
            System.out.println("JFR demo failed: " + e.getMessage());
        }
    }
}
