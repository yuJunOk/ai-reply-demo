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
public class CpuLoadTrendVisualizer implements MetricsVisualizer {

    @Override
    public MetricsChartType getType() { return MetricsChartType.CPU_LOAD_TREND; }

    @Override
    public byte[] visualize(MetricsVisualizerContext context) {
        MetricsRepository repository = context.getRepository();
        List<SystemMetrics> history = repository.getHistory();
        if (history.size() < 2) {
            return null;
        }

        XYChart chart = new XYChartBuilder().width(450).height(300).title("CPU 负载趋势 (%)").build();
        chart.getStyler().setChartBackgroundColor(Color.WHITE);
        chart.getStyler().setLegendVisible(false);

        List<Double> yData = history.stream().map(m -> m.getCpuLoad() * 100).collect(Collectors.toList());
        chart.addSeries("CPU Load", IntStream.range(0, yData.size()).boxed().collect(Collectors.toList()), yData);

        try { return BitmapEncoder.getBitmapBytes(chart, BitmapEncoder.BitmapFormat.PNG); }
        catch (IOException e) { return null; }
    }
}