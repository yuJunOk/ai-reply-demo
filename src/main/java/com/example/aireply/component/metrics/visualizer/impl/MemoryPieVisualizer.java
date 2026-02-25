package com.example.aireply.component.metrics.visualizer.impl;

import com.example.aireply.common.model.bo.monitoring.SystemMetrics;
import com.example.aireply.component.metrics.visualizer.MetricsChartType;
import com.example.aireply.component.metrics.visualizer.MetricsVisualizer;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;

/**
 * 内存使用饼图可视化器
 */
@Component
public class MemoryPieVisualizer implements MetricsVisualizer {
    @Override
    public MetricsChartType getType() {
        return MetricsChartType.MEMORY_PIE;
    }

    @Override
    public byte[] visualize(SystemMetrics metrics) {
        if (metrics == null) {
            return null;
        }
        PieChart chart = new PieChartBuilder()
                .width(400).height(300)
                .title("JVM 内存使用情况")
                .build();
        
        chart.getStyler().setChartTitleVisible(true);
        chart.getStyler().setLegendVisible(true);
        chart.getStyler().setLabelsVisible(true);
        chart.getStyler().setChartBackgroundColor(Color.WHITE);
        
        chart.addSeries("已用内存", metrics.getUsedHeapMb());
        chart.addSeries("剩余可用", Math.max(0, metrics.getMaxHeapMb() - metrics.getUsedHeapMb()));
        
        try {
            return BitmapEncoder.getBitmapBytes(chart, BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException e) {
            return null;
        }
    }
}