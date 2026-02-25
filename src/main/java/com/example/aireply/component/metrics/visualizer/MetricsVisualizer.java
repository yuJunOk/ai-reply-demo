package com.example.aireply.component.metrics.visualizer;

import com.example.aireply.common.model.bo.monitoring.SystemMetrics;

/**
 * 监控数据可视化接口
 */
public interface MetricsVisualizer {
    /** 声明自己属于哪种图表类型 */
    MetricsChartType getType();
    /** 生成图表的字节数组 */
    byte[] visualize(SystemMetrics metrics);
}