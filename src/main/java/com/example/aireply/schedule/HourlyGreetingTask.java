package com.example.aireply.schedule;

import com.example.aireply.common.model.bo.monitoring.SystemMetrics;
import com.example.aireply.component.notification.EmailSender;
import com.example.aireply.util.SystemMetricsCollector;
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

@Slf4j
@Component
public class HourlyGreetingTask {
    @Resource
    private EmailSender emailSender;

    @Value("${greeting.mail.to:}")
    private String greetingTo;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Scheduled(cron = "0 0 * * * ?")
    public void sendHourlyGreeting() {
        try {
            if (greetingTo == null || greetingTo.isBlank()) {
                log.warn("greeting.mail.to is blank, skip hourly greeting email");
                return;
            }
            String subject = "每小时问候";
            String html = buildGreetingHtml();
            emailSender.sendHtmlMail(subject, html, greetingTo);
        } catch (IOException e) {
            emailSender.sendBugReport(e);
        }
    }

    private String buildGreetingHtml() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        String time = now.format(FORMATTER);
        SystemMetrics metrics = SystemMetricsCollector.collect();
        String systemStatus = metrics.formatForMail();
        String template = loadTemplate("templates/hourly-greeting.html");
        template = template.replace("${time}", time);
        template = template.replace("${systemStatus}", systemStatus);
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
