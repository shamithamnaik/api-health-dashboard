package com.example.apimonitor.controller;

import com.example.apimonitor.dto.ApiStatusDto;
import com.example.apimonitor.model.CheckLog;
import com.example.apimonitor.service.ApiConfigService;
import com.example.apimonitor.service.ApiQueryService;
import com.example.apimonitor.service.MonitorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** Role rules live in SecurityConfig, so this class stays clean. */
@RestController
@RequestMapping("/api/apis")
public class ApiController {
    private final ApiQueryService query;
    private final MonitorService monitor;
    private final ApiConfigService config;

    public ApiController(ApiQueryService query, MonitorService monitor, ApiConfigService config) {
        this.query = query;
        this.monitor = monitor;
        this.config = config;
    }

    @GetMapping
    public List<ApiStatusDto> list(@RequestParam(required = false) String env,
                                   @RequestParam(required = false) String status,
                                   @RequestParam(required = false) String type) {
        return query.list(env, status, type);
    }

    @GetMapping("/filters")
    public Map<String, List<String>> filters() {
        return query.filters();
    }

    @GetMapping("/{id}/logs")
    public List<CheckLog> logs(@PathVariable Long id, @RequestParam(defaultValue = "50") int limit) {
        return query.logs(id, limit);
    }

    @PostMapping("/{id}/check")
    public ResponseEntity<Void> check(@PathVariable Long id) {
        monitor.checkOne(id);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/check-all")
    public ResponseEntity<Void> checkAll() {
        config.sync();
        monitor.checkAll();
        return ResponseEntity.accepted().build();
    }
}
