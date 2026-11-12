package com.example.apimonitor.service;

import com.example.apimonitor.dto.CheckResultEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CheckResultProducer {
    private final KafkaTemplate<String, String> kafka;
    private final ObjectMapper mapper;
    private final String topic;

    public CheckResultProducer(KafkaTemplate<String, String> kafka, ObjectMapper mapper,
                               @Value("${monitor.topic}") String topic) {
        this.kafka = kafka;
        this.mapper = mapper;
        this.topic = topic;
    }

    public void send(CheckResultEvent event) {
        try {
            kafka.send(topic, String.valueOf(event.apiId()), mapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
