package com.example.apimonitor.dto;

import java.time.Instant;

/** Message published to Kafka after every check. */
public record CheckResultEvent(Long apiId, String status, Integer httpStatus,
                               long responseTimeMs, String message, Instant checkedAt) {}
