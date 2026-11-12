package com.example.apimonitor.service;

import com.example.apimonitor.dto.CheckResultEvent;
import com.example.apimonitor.model.CheckLog;
import com.example.apimonitor.repository.ApiEndpointRepository;
import com.example.apimonitor.repository.CheckLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Reads results from Kafka -> writes the log row and updates the "latest status" on the API. */
@Service
public class CheckResultConsumer {
    private final ApiEndpointRepository apis;
    private final CheckLogRepository logs;
    private final ObjectMapper mapper;

    public CheckResultConsumer(ApiEndpointRepository apis, CheckLogRepository logs, ObjectMapper mapper) {
        this.apis = apis;
        this.logs = logs;
        this.mapper = mapper;
    }

    @KafkaListener(topics = "${monitor.topic}", groupId = "api-monitor")
    @Transactional
    public void onResult(String json) throws Exception {
        CheckResultEvent ev = mapper.readValue(json, CheckResultEvent.class);

        String msg = ev.message() != null && ev.message().length() > 990 ? ev.message().substring(0, 990) : ev.message();

        CheckLog row = new CheckLog();
        row.setApiId(ev.apiId());
        row.setStatus(ev.status());
        row.setHttpStatus(ev.httpStatus());
        row.setResponseTimeMs(ev.responseTimeMs());
        row.setMessage(msg);
        row.setCheckedAt(ev.checkedAt());
        logs.save(row);

        apis.findById(ev.apiId()).ifPresent(a -> {
            a.setLastStatus(ev.status());
            a.setLastHttpStatus(ev.httpStatus());
            a.setLastResponseMs(ev.responseTimeMs());
            a.setLastCheckedAt(ev.checkedAt());
            a.setLastMessage(msg);
            apis.save(a);
        });
    }
}
