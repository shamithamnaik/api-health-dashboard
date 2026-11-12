package com.example.apimonitor.service;

import com.example.apimonitor.model.ApiDefinition;
import com.example.apimonitor.model.ApiEndpoint;
import com.example.apimonitor.repository.ApiEndpointRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/** Reads config/apis.yml (the single source of APIs) and syncs it into the DB. */
@Service
public class ApiConfigService {
    private static final Logger log = LoggerFactory.getLogger(ApiConfigService.class);

    private final ApiEndpointRepository repo;
    private final Resource apisFile;
    private final ObjectMapper yaml = new ObjectMapper(new YAMLFactory())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public ApiConfigService(ApiEndpointRepository repo, @Value("${monitor.apis-file}") Resource apisFile) {
        this.repo = repo;
        this.apisFile = apisFile;
    }

    @Transactional
    public void sync() {
        try (InputStream in = apisFile.getInputStream()) {
            ApiDefinition.File cfg = yaml.readValue(in, ApiDefinition.File.class);
            Set<Long> seen = new HashSet<>();

            for (ApiDefinition d : cfg.getApis()) {
                String env = d.getEnv().toUpperCase();
                ApiEndpoint e = repo.findByNameAndEnv(d.getName(), env).orElseGet(ApiEndpoint::new);
                e.setName(d.getName());
                e.setEnv(env);
                e.setType(d.getType().toUpperCase());
                e.setUrl(d.getUrl());
                e.setMethod(d.getMethod());
                e.setBody(d.getBody());
                e.setSoapAction(d.getSoapAction());
                e.setExpectedStatus(d.getExpectedStatus());
                e.setTimeoutMs(d.getTimeoutMs());
                e.setEnabled(true);
                seen.add(repo.save(e).getId());
            }
            // APIs removed from the yaml are switched off (history is kept)
            repo.findAll().stream().filter(e -> !seen.contains(e.getId()) && e.isEnabled())
                    .forEach(e -> { e.setEnabled(false); repo.save(e); });
        } catch (Exception ex) {
            log.error("Could not read {}: {}", apisFile, ex.getMessage());
        }
    }
}
