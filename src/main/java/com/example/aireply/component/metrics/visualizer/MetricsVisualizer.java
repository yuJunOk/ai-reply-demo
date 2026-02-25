package com.example.aireply.component.metrics.visualizer;

/**
 * 监控数据可视化接口
 */
public interface MetricsVisualizer {
    /** 声明自己属于哪种图表类型 */
    MetricsChartType getType();

    /** 
     * 生成图表的字节数组 
     * @param context 可视化上下文，包含当前数据、历史数据及环境信息
     */
    byte[] visualize(MetricsVisualizerContext context);
}