package com.example.apimonitor.repository;

import com.example.apimonitor.model.ApiEndpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ApiEndpointRepository extends JpaRepository<ApiEndpoint, Long> {
    Optional<ApiEndpoint> findByNameAndEnv(String name, String env);
    List<ApiEndpoint> findByEnabledTrue();
}
