package com.example.apimonitor.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

/** One row per health check ever executed. Grafana reads this table too. */
@Entity
@Getter @Setter
@Table(indexes = @Index(columnList = "apiId, checkedAt"))
public class CheckLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long apiId;
    private String status;
    private Integer httpStatus;
    private long responseTimeMs;
    @Column(length = 1000)
    private String message;
    private Instant checkedAt;
}
