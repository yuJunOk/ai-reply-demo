package com.example.aireply.util;

import com.example.aireply.common.model.bo.monitoring.SystemMetrics;
import com.sun.management.OperatingSystemMXBean;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.style.PieStyler;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author 13758
 */
@Component
public class SystemMetricsCollector {

    public SystemMetrics collect() throws IOException {
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

        long uptimeMillis = ManagementFactory.getRuntimeMXBean().getUptime();

        Double cpuTemp = readCpuTemperatureLinux();

        return new SystemMetrics(hostname, os, osArch, cpuLoad, usedHeapMb, maxHeapMb, heapUsagePercent, diskTotalGb, diskUsedGb, diskUsagePercent, uptimeMillis, cpuTemp);
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

    /**
     * 生成内存使用情况的饼图字节数组
     */
    public byte[] generateMemoryPieChartImage(SystemMetrics metrics) {
        if (metrics == null) {
            return null;
        }

        // 创建饼图
        PieChart chart = new PieChartBuilder()
                .width(400)
                .height(300)
                .title("JVM 内存使用情况 (MB)")
                .build();

        // 样式设置
        chart.getStyler().setChartTitleVisible(true);
        chart.getStyler().setLegendVisible(true);
        // 使用最通用的标签显示方法，避开版本差异明显的 AnnotationType
        chart.getStyler().setLabelsVisible(true);
        chart.getStyler().setChartBackgroundColor(Color.WHITE);

        // 添加数据
        chart.addSeries("已用内存", metrics.getUsedHeapMb());
        chart.addSeries("剩余可用", Math.max(0, metrics.getMaxHeapMb() - metrics.getUsedHeapMb()));

        try {
            // 导出为 PNG 字节数组
            return BitmapEncoder.getBitmapBytes(chart, BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException e) {
            return null;
        }
    }
}

