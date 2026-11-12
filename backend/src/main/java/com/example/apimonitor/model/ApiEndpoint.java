package com.example.apimonitor.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

/** One monitored API in one environment. Definition comes from apis.yml, last result is updated by the Kafka consumer. */
@Entity
@Getter @Setter
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name", "env"}))
public class ApiEndpoint {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String env;
    private String type;            // REST | SOAP
    @Column(length = 2000)
    private String url;
    private String method;
    @Column(columnDefinition = "TEXT")
    private String body;
    private String soapAction;
    private int expectedStatus;
    private int timeoutMs;
    private boolean enabled = true;

    // latest result (denormalised so the dashboard list is one cheap query)
    private String lastStatus = "UNKNOWN";   // UP | DOWN | UNKNOWN
    private Integer lastHttpStatus;
    private Long lastResponseMs;
    private Instant lastCheckedAt;
    @Column(length = 1000)
    private String lastMessage;
}
