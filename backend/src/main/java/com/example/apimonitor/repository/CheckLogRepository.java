package com.example.apimonitor.repository;

import com.example.apimonitor.model.CheckLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CheckLogRepository extends JpaRepository<CheckLog, Long> {
    List<CheckLog> findByApiIdOrderByCheckedAtDesc(Long apiId, Pageable page);
}
