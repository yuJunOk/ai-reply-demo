package com.example.aireply.service;

import com.example.aireply.component.HtmlTemplateEngine;
import com.example.aireply.component.camera.RaspberryPiCameraCollector;
import com.example.aireply.component.metrics.MetricsRepository;
import com.example.aireply.component.metrics.SystemMetricsCollector;
import com.example.aireply.component.metrics.model.SystemMetrics;
import com.example.aireply.component.metrics.visualizer.MetricsChartType;
import com.example.aireply.component.metrics.visualizer.MetricsVisualizerContext;
import com.example.aireply.component.metrics.visualizer.MetricsVisualizerFactory;
import com.example.aireply.component.notification.EmailSender;
import com.example.aireply.component.tyme.TymeCollector;
import com.example.aireply.component.tyme.model.TymeInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 时空观测服务：规范的业务编排层
 */
@Slf4j
@Service
public class ChronosWatcherService {
    @Resource private EmailSender emailSender;
    @Resource private SystemMetricsCollector metricsCollector;
    @Resource private MetricsVisualizerFactory visualizerFactory;
    @Resource private MetricsRepository metricsRepository;
    @Resource private TymeCollector tymeCollector;
    @Resource private RaspberryPiCameraCollector cameraCollector;
    @Resource private HtmlTemplateEngine templateEngine;

    @Value("${mail.manager}")
    private String manageEmail;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void watch() {
        if (manageEmail == null || manageEmail.isBlank()) {
            return;
        }

        try {
            // 1. 采集数据
            SystemMetrics metrics = metricsCollector.collect();
            TymeInfo tyme = tymeCollector.collect();

            // 2. 采集图表与照片
            Map<String, byte[]> images = collectImages(metrics);

            // 3. 构建变量并渲染 HTML
            Map<String, String> vars = buildVariables(metrics, tyme, images);
            String html = templateEngine.render("templates/hourly-report.html", vars);

            // 4. 发送邮件
            emailSender.sendHtmlMailWithImages("时空守望 · 每小时态势报表", html, images, manageEmail);
            log.info("Chronos watch report sent successfully.");
        } catch (Exception e) {
            log.error("Chronos watch failed", e);
            emailSender.sendBugReport(e);
        }
    }

    private Map<String, byte[]> collectImages(SystemMetrics metrics) {
        Map<String, byte[]> images = new HashMap<>();
        MetricsVisualizerContext context = MetricsVisualizerContext.builder()
                .currentMetrics(metrics).repository(metricsRepository).build();

        visualizerFactory.getAllVisualizers().forEach(v -> {
            byte[] data = v.visualize(context);
            if (data != null) {
                images.put(v.getType().getCid(), data);
            }
        });

        if (cameraCollector.isCameraAvailable()) {
            byte[] photo = cameraCollector.capturePhoto();
            if (photo != null) {
                images.put(MetricsChartType.RPI_CAMERA.getCid(), photo);
            }
        }
        return images;
    }

    private Map<String, String> buildVariables(SystemMetrics metrics, TymeInfo tyme, Map<String, byte[]> images) {
        Map<String, String> vars = new HashMap<>();
        vars.put("time", LocalDateTime.now().format(FORMATTER));
        vars.put("systemStatus", metrics.formatForMail());

        // 历法相关
        vars.put("lunarDate", tyme.getLunarYear() + tyme.getLunarMonth() + tyme.getLunarDay());
        vars.put("week", tyme.getWeek());
        vars.put("animal", tyme.getAnimal());
        vars.put("constellation", tyme.getConstellation());
        vars.put("solarTerm", tyme.getSolarTerm());
        vars.put("festivals", tyme.getFestivals());
        vars.put("lunarSeason", tyme.getLunarSeason());
        vars.put("moonPhase", tyme.getMoonPhase());

        // 此时信息
        vars.put("currentHour", tyme.getCurrentHour());
        vars.put("hourRange", tyme.getCurrentHourRange());
        vars.put("liuRen", tyme.getLiuRen());
        vars.put("liuRenLuck", tyme.getLiuRenLuck());
        vars.put("liuRenElement", tyme.getLiuRenElement());

        // 宜忌信息
        vars.put("dayYi", String.join(" ", tyme.getDayYi()));
        vars.put("dayJi", String.join(" ", tyme.getDayJi()));
        vars.put("hourYi", String.join(" ", tyme.getHourYi()));
        vars.put("hourJi", String.join(" ", tyme.getHourJi()));
        vars.put("pengZu", tyme.getPengZu());

        // 文化背景
        vars.put("wuXing", tyme.getWuXing());
        vars.put("baZi", tyme.getBaZi());

        // --- 仅传递条件布尔值 (String形式) ---
        vars.put("hasCamera", String.valueOf(images.containsKey(MetricsChartType.RPI_CAMERA.getCid())));
        vars.put("hasMemoryChart", String.valueOf(images.containsKey(MetricsChartType.MEMORY_PIE.getCid())));
        vars.put("hasCpuLoadTrend", String.valueOf(images.containsKey(MetricsChartType.CPU_LOAD_TREND.getCid())));
        vars.put("hasCpuTempTrend", String.valueOf(images.containsKey(MetricsChartType.CPU_TEMP_TREND.getCid())));

        return vars;
    }
}