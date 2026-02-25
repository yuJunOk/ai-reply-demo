package com.example.aireply.component.metrics.provider;

import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LinuxMetricsProvider implements MetricsProvider {
    @Override
    public long getSystemUptime() {
        try {
            Path p = Paths.get("/proc/uptime");
            if (Files.exists(p)) {
                String content = Files.readString(p).split("\\s+")[0];
                return (long) (Double.parseDouble(content) * 1000);
            }
        } catch (Exception ignored) {}
        return ManagementFactory.getRuntimeMXBean().getUptime();
    }

    @Override
    public Double getCpuTemperature() {
        String path = "/sys/class/thermal/thermal_zone0/temp";
        try {
            Path p = Paths.get(path);
            if (Files.exists(p)) {
                String content = Files.readString(p).trim();
                return Double.parseDouble(content) / 1000.0;
            }
        } catch (Exception ignored) {}
        return null;
    }
}