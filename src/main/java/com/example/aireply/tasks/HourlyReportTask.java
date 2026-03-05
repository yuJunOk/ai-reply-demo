package com.example.aireply.tasks;

import com.example.aireply.service.ChronosWatcherService;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 时空守望定时任务
 */
@Component
public class HourlyReportTask {

    @Resource
    private ChronosWatcherService chronosWatcherService;

    @Scheduled(cron = "0 0 * * * ?")
    public void execute() {
        chronosWatcherService.watch();
    }
}
