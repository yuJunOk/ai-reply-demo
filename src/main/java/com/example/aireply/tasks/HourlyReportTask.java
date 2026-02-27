package com.example.aireply.tasks;

import com.example.aireply.component.metrics.model.SystemMetrics;
import com.example.aireply.component.metrics.MetricsRepository;
import com.example.aireply.component.metrics.SystemMetricsCollector;
import com.example.aireply.component.metrics.visualizer.MetricsVisualizer;
import com.example.aireply.component.metrics.visualizer.MetricsVisualizerContext;
import com.example.aireply.component.metrics.visualizer.MetricsVisualizerFactory;
import com.example.aireply.component.notification.EmailSender;
import com.example.aireply.component.tyme.TymeCollector;
import com.example.aireply.component.tyme.model.TymeInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class HourlyReportTask {
    @Resource
    private EmailSender emailSender;

    @Resource
    private SystemMetricsCollector systemMetricsCollector;

    @Resource
    private MetricsVisualizerFactory visualizerFactory;

    @Resource
    private MetricsRepository metricsRepository;

    @Resource
    private TymeCollector tymeCollector;

    @Value("${mail.manager}")
    private String manageEmail;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Scheduled(cron = "0 0 * * * ?")
    public void sendHourlyReport() {
        try {
            if (manageEmail == null || manageEmail.isBlank()) {
                log.warn("mail.manager is blank, skip hourly report email");
                return;
            }

            SystemMetrics metrics = systemMetricsCollector.collect();

            // 上下文
            MetricsVisualizerContext metricsVisualizerContext = MetricsVisualizerContext.builder()
                    .currentMetrics(metrics).repository(metricsRepository).build();

            // 使用工厂获取所有可视化器并生成图表
            Map<String, byte[]> images = new HashMap<>();
            for (MetricsVisualizer visualizer : visualizerFactory.getAllVisualizers()) {
                byte[] chartData = visualizer.visualize(metricsVisualizerContext);
                if (chartData != null) {
                    // 通过枚举获取统一定义的 CID
                    images.put(visualizer.getType().getCid(), chartData);
                }
            }

            String subject = "每小时报表";
            TymeInfo tymeInfo = tymeCollector.collect();
            String html = buildReportHtml(metrics, tymeInfo);
            emailSender.sendHtmlMailWithImages(subject, html, images, manageEmail);
        } catch (IOException e) {
            emailSender.sendBugReport(e);
        }
    }

    private String buildReportHtml(SystemMetrics metrics, TymeInfo tyme) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        String time = now.format(FORMATTER);
        String systemStatus = metrics.formatForMail();
        String template = loadTemplate("templates/hourly-report.html");
        
        // 替换基础信息
        template = template.replace("${time}", time);
        template = template.replace("${systemStatus}", systemStatus);
        
        // 替换历法信息
        template = template.replace("${lunarDate}", tyme.getLunarYear() + tyme.getLunarMonth() + tyme.getLunarDay());
        template = template.replace("${week}", tyme.getWeek());
        template = template.replace("${animal}", tyme.getAnimal());
        template = template.replace("${constellation}", tyme.getConstellation());
        template = template.replace("${solarTerm}", tyme.getSolarTerm());
        template = template.replace("${festivals}", tyme.getFestivals().isEmpty() ? "无" : tyme.getFestivals());
        
        // 此时信息
        template = template.replace("${currentHour}", tyme.getCurrentHour());
        template = template.replace("${hourRange}", tyme.getCurrentHourRange());
        template = template.replace("${liuRen}", tyme.getLiuRen());
        template = template.replace("${liuRenLuck}", tyme.getLiuRenLuck());
        template = template.replace("${liuRenElement}", tyme.getLiuRenElement());
        
        // 宜忌信息
        template = template.replace("${dayYi}", String.join(" ", tyme.getDayYi()));
        template = template.replace("${dayJi}", String.join(" ", tyme.getDayJi()));
        template = template.replace("${hourYi}", String.join(" ", tyme.getHourYi()));
        template = template.replace("${hourJi}", String.join(" ", tyme.getHourJi()));
        template = template.replace("${pengZu}", tyme.getPengZu());
        
        // 文化背景
        template = template.replace("${wuXing}", tyme.getWuXing());
        template = template.replace("${baZi}", tyme.getBaZi());
        template = template.replace("${lunarSeason}", tyme.getLunarSeason());
        template = template.replace("${moonPhase}", tyme.getMoonPhase());
        
        return template;
    }

    private String loadTemplate(String path) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new IllegalStateException("Template not found: " + path);
            }
            byte[] bytes = inputStream.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load template: " + path, e);
        }
    }
}
