package com.example.apimonitor.service;

import com.example.apimonitor.model.ApiEndpoint;
import com.example.apimonitor.repository.ApiEndpointRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/** Runs checks and publishes the results to Kafka. */
@Service
public class MonitorService {
    private final ApiEndpointRepository repo;
    private final HealthCheckService checker;
    private final CheckResultProducer producer;

    public MonitorService(ApiEndpointRepository repo, HealthCheckService checker, CheckResultProducer producer) {
        this.repo = repo;
        this.checker = checker;
        this.producer = producer;
    }

    public void checkAll() {
        repo.findByEnabledTrue().parallelStream().forEach(this::check);
    }

    public void checkOne(Long id) {
        ApiEndpoint api = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "API not found"));
        check(api);
    }

    private void check(ApiEndpoint api) {
        producer.send(checker.check(api));
    }
}
