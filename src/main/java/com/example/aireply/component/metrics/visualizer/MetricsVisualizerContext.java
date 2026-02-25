package com.example.aireply.component.metrics.visualizer;

import com.example.aireply.common.model.bo.monitoring.SystemMetrics;
import com.example.aireply.component.metrics.MetricsRepository;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 可视化上下文对象
 * 封装绘图所需的所有环境信息，增强接口扩展性
 */
@Getter
@Builder
public class MetricsVisualizerContext {
    /** 当前最新指标 */
    private final SystemMetrics currentMetrics;
    
    /** 历史数据仓库 */
    private final MetricsRepository repository;
    
    // 未来可以轻松添加更多字段，如：
    // private final Map<String, Object> config;
    // private final String theme;
}