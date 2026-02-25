package com.example.aireply.component.metrics.provider;

import java.lang.management.ManagementFactory;

public class WindowsMetricsProvider implements MetricsProvider {
    @Override
    public long getSystemUptime() {
        return ManagementFactory.getRuntimeMXBean().getUptime();
    }

    @Override
    public Double getCpuTemperature() {
        return null;
    }
}