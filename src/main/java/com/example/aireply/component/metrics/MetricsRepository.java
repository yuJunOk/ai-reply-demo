package com.example.aireply.component.metrics;

import com.example.aireply.component.metrics.model.SystemMetrics;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
public class MetricsRepository {

    // 缓存最近一小时的数据，假设每 5 分钟存一次，保存 12 条左右
    private final Cache<String, List<SystemMetrics>> historyCache = Caffeine.newBuilder()
            .expireAfterWrite(2, TimeUnit.HOURS) // 稍微多留一点余地
            .build();

    private static final String HISTORY_KEY = "hourly_history";

    /**
     * 添加一条最新的监控数据
     */
    public synchronized void save(SystemMetrics metrics) {
        List<SystemMetrics> history = historyCache.get(HISTORY_KEY, k -> new ArrayList<>());
        if (history == null) {
            return;
        }
        
        history.add(metrics);
        
        // 保持最近 24 条记录（如果是 5 分钟存一次，2 小时就是 24 条）
        if (history.size() > 24) {
            history.remove(0);
        }
        
        historyCache.put(HISTORY_KEY, history);
    }

    /**
     * 获取所有历史数据
     */
    public List<SystemMetrics> getHistory() {
        List<SystemMetrics> history = historyCache.getIfPresent(HISTORY_KEY);
        return history != null ? new ArrayList<>(history) : new ArrayList<>();
    }
}