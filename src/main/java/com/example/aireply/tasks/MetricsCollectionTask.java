package com.example.aireply.tasks;

import com.example.aireply.component.metrics.model.SystemMetrics;
import com.example.aireply.component.metrics.SystemMetricsCollector;
import com.example.aireply.component.metrics.MetricsRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MetricsCollectionTask {

    @Resource
    private SystemMetricsCollector collector;

    @Resource
    private MetricsRepository repository;

    /**
     * 每 5 分钟自动采集一次系统指标并存入本地缓存
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void collectMetrics() {
        try {
            SystemMetrics metrics = collector.collect();
            if (metrics != null) {
                repository.save(metrics);
                log.debug("成功采集并保存系统指标");
            }
        } catch (Exception e) {
            log.error("定时采集指标失败", e);
        }
    }
}