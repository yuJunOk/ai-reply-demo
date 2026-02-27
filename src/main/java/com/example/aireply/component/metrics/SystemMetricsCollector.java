package com.example.aireply.component.metrics;

import com.example.aireply.component.metrics.model.SystemMetrics;
import com.example.aireply.component.metrics.provider.LinuxMetricsProvider;
import com.example.aireply.component.metrics.provider.MetricsProvider;
import com.example.aireply.component.metrics.provider.WindowsMetricsProvider;
import com.sun.management.OperatingSystemMXBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;

/**
 * 系统指标采集组件
 * 负责采集与可视化逻辑
 */
@Component
public class SystemMetricsCollector {

    private final MetricsProvider provider;

    public SystemMetricsCollector() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("linux")) {
            this.provider = new LinuxMetricsProvider();
        } else {
            this.provider = new WindowsMetricsProvider();
        }
    }

    public SystemMetrics collect() {
        try {
            String hostname = resolveHostname();
            String os = System.getProperty("os.name") + " " + System.getProperty("os.version");
            String osArch = System.getProperty("os.arch");

            OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            double cpuLoad = osBean.getCpuLoad();

            long usedHeap = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long maxHeap = Runtime.getRuntime().maxMemory();
            long usedHeapMb = usedHeap / (1024 * 1024);
            long maxHeapMb = maxHeap / (1024 * 1024);
            double heapUsagePercent = maxHeap > 0 ? usedHeap * 100.0 / maxHeap : -1;

            FileStore rootStore = detectRootFileStore();
            long diskTotalGb = rootStore.getTotalSpace() / (1024 * 1024 * 1024);
            long diskUsedGb = (rootStore.getTotalSpace() - rootStore.getUnallocatedSpace()) / (1024 * 1024 * 1024);
            double diskUsagePercent = rootStore.getTotalSpace() > 0 ? (rootStore.getTotalSpace() - rootStore.getUnallocatedSpace()) * 100.0 / rootStore.getTotalSpace() : -1;

            // 调用策略类获取差异化指标
            long uptimeMillis = provider.getSystemUptime();
            Double cpuTemp = provider.getCpuTemperature();

            return new SystemMetrics(hostname, os, osArch, cpuLoad, usedHeapMb, maxHeapMb, heapUsagePercent, diskTotalGb, diskUsedGb, diskUsagePercent, uptimeMillis, cpuTemp);
        } catch (Exception e) {
            return null;
        }
    }

    private String resolveHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }

    private FileStore detectRootFileStore() throws IOException {
        for (FileStore store : FileSystems.getDefault().getFileStores()) {
            return store;
        }
        throw new IOException("No file store found");
    }
}