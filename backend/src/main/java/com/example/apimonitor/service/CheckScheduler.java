package com.example.apimonitor.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CheckScheduler {
    private final ApiConfigService config;
    private final MonitorService monitor;

    public CheckScheduler(ApiConfigService config, MonitorService monitor) {
        this.config = config;
        this.monitor = monitor;
    }

    @Scheduled(initialDelay = 3000, fixedDelayString = "${monitor.interval-ms}")
    public void run() {
        config.sync();       // pick up any change made to apis.yml
        monitor.checkAll();
    }
}
