package com.example.aireply.component.metrics.visualizer.impl;

import com.example.aireply.common.model.bo.monitoring.SystemMetrics;
import com.example.aireply.component.metrics.MetricsRepository;
import com.example.aireply.component.metrics.visualizer.MetricsChartType;
import com.example.aireply.component.metrics.visualizer.MetricsVisualizer;
import com.example.aireply.component.metrics.visualizer.MetricsVisualizerContext;
import jakarta.annotation.Resource;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class CpuTempTrendVisualizer implements MetricsVisualizer {
    @Resource
    private MetricsRepository repository;

    @Override
    public MetricsChartType getType() { return MetricsChartType.CPU_TEMP_TREND; }

    @Override
    public byte[] visualize(MetricsVisualizerContext context) {
        MetricsRepository repository = context.getRepository();
        List<SystemMetrics> history = repository.getHistory();
        List<Double> yData = history.stream()
                .map(SystemMetrics::getCpuTempCelsius)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
        
        if (yData.size() < 2) {
            return null;
        }

        XYChart chart = new XYChartBuilder().width(450).height(300).title("CPU 温度趋势 (℃)").build();
        chart.getStyler().setChartBackgroundColor(Color.WHITE);
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setPlotGridLinesColor(new Color(240, 240, 240));

        chart.addSeries("Temperature", IntStream.range(0, yData.size()).boxed().collect(Collectors.toList()), yData);

        try { return BitmapEncoder.getBitmapBytes(chart, BitmapEncoder.BitmapFormat.PNG); }
        catch (IOException e) { return null; }
    }
}