package com.example.aireply.util;

import com.example.aireply.common.model.bo.monitoring.SystemMetrics;
import com.sun.management.OperatingSystemMXBean;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SystemMetricsCollector {
    private SystemMetricsCollector() {
    }

    public static SystemMetrics collect() throws IOException {
        String hostname = resolveHostname();
        String os = System.getProperty("os.name") + " " + System.getProperty("os.version");
        String osArch = System.getProperty("os.arch");

        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double cpuLoad = osBean.getSystemCpuLoad();

        long usedHeap = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long maxHeap = Runtime.getRuntime().maxMemory();
        long usedHeapMb = usedHeap / (1024 * 1024);
        long maxHeapMb = maxHeap / (1024 * 1024);
        double heapUsagePercent = maxHeap > 0 ? usedHeap * 100.0 / maxHeap : -1;

        FileStore rootStore = detectRootFileStore();
        long diskTotalGb = rootStore.getTotalSpace() / (1024 * 1024 * 1024);
        long diskUsedGb = (rootStore.getTotalSpace() - rootStore.getUnallocatedSpace()) / (1024 * 1024 * 1024);
        double diskUsagePercent = rootStore.getTotalSpace() > 0 ? (rootStore.getTotalSpace() - rootStore.getUnallocatedSpace()) * 100.0 / rootStore.getTotalSpace() : -1;

        long uptimeMillis = ManagementFactory.getRuntimeMXBean().getUptime();
        long uptimeMinutes = uptimeMillis / (60 * 1000);

        Double cpuTemp = readCpuTemperatureLinux();

        return new SystemMetrics(hostname, os, osArch, cpuLoad, usedHeapMb, maxHeapMb, heapUsagePercent, diskTotalGb, diskUsedGb, diskUsagePercent, uptimeMinutes, cpuTemp);
    }

    private static String resolveHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }

    private static FileStore detectRootFileStore() {
        for (FileStore store : FileSystems.getDefault().getFileStores()) {
            return store;
        }
        throw new IllegalStateException("No file store found");
    }

    private static Double readCpuTemperatureLinux() {
        String path = "/sys/class/thermal/thermal_zone0/temp";
        try {
            Path p = Paths.get(path);
            if (!Files.exists(p)) {
                return null;
            }
            String content = Files.readString(p).trim();
            if (content.isEmpty()) {
                return null;
            }
            double milli = Double.parseDouble(content);
            return milli / 1000.0;
        } catch (Exception e) {
            return null;
        }
    }
}

