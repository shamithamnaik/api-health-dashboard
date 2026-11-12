package com.example.apimonitor.service;

import com.example.apimonitor.dto.ApiStatusDto;
import com.example.apimonitor.model.ApiEndpoint;
import com.example.apimonitor.model.CheckLog;
import com.example.apimonitor.repository.ApiEndpointRepository;
import com.example.apimonitor.repository.CheckLogRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class ApiQueryService {
    private final ApiEndpointRepository repo;
    private final CheckLogRepository logs;

    public ApiQueryService(ApiEndpointRepository repo, CheckLogRepository logs) {
        this.repo = repo;
        this.logs = logs;
    }

    /** Any filter may be null/blank = "all". */
    public List<ApiStatusDto> list(String env, String status, String type) {
        return repo.findByEnabledTrue().stream()
                .filter(a -> matches(a.getEnv(), env))
                .filter(a -> matches(a.getLastStatus(), status))
                .filter(a -> matches(a.getType(), type))
                .sorted(Comparator.comparing(ApiEndpoint::getEnv).thenComparing(ApiEndpoint::getName))
                .map(ApiStatusDto::from)
                .toList();
    }

    /** Values for the filter drop-downs, built from the config so they are always in sync. */
    public Map<String, List<String>> filters() {
        List<ApiEndpoint> all = repo.findByEnabledTrue();
        return Map.of(
                "envs", all.stream().map(ApiEndpoint::getEnv).distinct().sorted().toList(),
                "types", all.stream().map(ApiEndpoint::getType).distinct().sorted().toList());
    }

    public List<CheckLog> logs(Long apiId, int limit) {
        return logs.findByApiIdOrderByCheckedAtDesc(apiId, PageRequest.of(0, Math.min(Math.max(limit, 1), 200)));
    }

    private boolean matches(String value, String filter) {
        return filter == null || filter.isBlank() || filter.equalsIgnoreCase(value);
    }
}
