package com.example.aireply.component.metrics.provider;

/**
 * 操作系统指标提供者接口
 */
public interface MetricsProvider {
    long getSystemUptime();
    Double getCpuTemperature();
}