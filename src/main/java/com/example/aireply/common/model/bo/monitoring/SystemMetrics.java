package com.example.aireply.common.model.bo.monitoring;

import com.example.aireply.util.DurationUtils;
import lombok.Getter;

/**
 * @author 13758
 */
@Getter
public class SystemMetrics {
    private final String hostname;
    private final String os;
    private final String osArch;
    private final double cpuLoad;
    private final long usedHeapMb;
    private final long maxHeapMb;
    private final double heapUsagePercent;
    private final long diskTotalGb;
    private final long diskUsedGb;
    private final double diskUsagePercent;
    private final long uptimeMillis;
    private final Double cpuTempCelsius;

    public SystemMetrics(String hostname, String os, String osArch, double cpuLoad, long usedHeapMb, long maxHeapMb, double heapUsagePercent, long diskTotalGb, long diskUsedGb, double diskUsagePercent, long uptimeMillis, Double cpuTempCelsius) {
        this.hostname = hostname;
        this.os = os;
        this.osArch = osArch;
        this.cpuLoad = cpuLoad;
        this.usedHeapMb = usedHeapMb;
        this.maxHeapMb = maxHeapMb;
        this.heapUsagePercent = heapUsagePercent;
        this.diskTotalGb = diskTotalGb;
        this.diskUsedGb = diskUsedGb;
        this.diskUsagePercent = diskUsagePercent;
        this.uptimeMillis = uptimeMillis;
        this.cpuTempCelsius = cpuTempCelsius;
    }

    public String formatForMail() {
        StringBuilder sb = new StringBuilder();
        sb.append("主机: ").append(hostname).append(" (").append(os).append(", ").append(osArch).append(")").append("\n");
        sb.append("运行时长: ").append(DurationUtils.formatSmartLong(uptimeMillis)).append("\n");
        sb.append("CPU 负载: ");
        if (cpuLoad >= 0) {
            sb.append(String.format("%.1f%%", cpuLoad * 100));
        } else {
            sb.append("N/A");
        }
        sb.append("\n");
        sb.append("堆内存: ").append(usedHeapMb).append(" MB / ").append(maxHeapMb).append(" MB (").append(String.format("%.1f%%", heapUsagePercent)).append(")").append("\n");
        sb.append("磁盘: ").append(diskUsedGb).append(" GB / ").append(diskTotalGb).append(" GB (").append(String.format("%.1f%%", diskUsagePercent)).append(")").append("\n");
        if (cpuTempCelsius != null) {
            sb.append("CPU 温度: ").append(String.format("%.1f℃", cpuTempCelsius)).append("\n");
        }
        return sb.toString();
    }
}

