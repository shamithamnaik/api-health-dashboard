package com.example.apimonitor.dto;

import com.example.apimonitor.model.ApiEndpoint;
import java.time.Instant;

public record ApiStatusDto(Long id, String name, String env, String type, String url,
                           String status, Integer httpStatus, Long responseTimeMs,
                           Instant lastCheckedAt, String message) {
    public static ApiStatusDto from(ApiEndpoint e) {
        return new ApiStatusDto(e.getId(), e.getName(), e.getEnv(), e.getType(), e.getUrl(),
                e.getLastStatus(), e.getLastHttpStatus(), e.getLastResponseMs(),
                e.getLastCheckedAt(), e.getLastMessage());
    }
}
