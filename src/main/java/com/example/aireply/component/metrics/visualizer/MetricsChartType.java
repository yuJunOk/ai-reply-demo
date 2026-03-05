package com.example.aireply.component.metrics.visualizer;

import lombok.Getter;

@Getter
public enum MetricsChartType {
    /**
     *
     */
    MEMORY_PIE("memoryChart", "JVM内存饼图"),
    CPU_LOAD_TREND("cpuLoadTrend", "CPU负载趋势图"),
    CPU_TEMP_TREND("cpuTempTrend", "CPU温度趋势图"),
    RPI_CAMERA("rpiCamera", "树莓派实时照片");

    private final String cid;
    private final String description;

    MetricsChartType(String cid, String description) {
        this.cid = cid;
        this.description = description;
    }
}