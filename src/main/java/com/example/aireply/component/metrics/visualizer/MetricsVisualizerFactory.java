package com.example.aireply.component.metrics.visualizer;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MetricsVisualizerFactory {

    @Resource
    private List<MetricsVisualizer> visualizerList;

    private final Map<MetricsChartType, MetricsVisualizer> visualizerMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        visualizerMap.putAll(visualizerList.stream()
                .collect(Collectors.toMap(MetricsVisualizer::getType, Function.identity())));
    }

    /** 获取所有可用的可视化器 */
    public Collection<MetricsVisualizer> getAllVisualizers() {
        return visualizerMap.values();
    }

    /** 按需获取特定的可视化器 */
    public MetricsVisualizer getVisualizer(MetricsChartType type) {
        return visualizerMap.get(type);
    }
}