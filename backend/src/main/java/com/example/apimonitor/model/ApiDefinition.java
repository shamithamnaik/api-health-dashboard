package com.example.apimonitor.model;

import lombok.Data;
import java.util.List;

/** Mirrors one entry of config/apis.yml */
@Data
public class ApiDefinition {
    private String name;
    private String env;
    private String type = "REST";
    private String url;
    private String method = "GET";
    private String body;
    private String soapAction;
    private int expectedStatus = 200;
    private int timeoutMs = 5000;

    /** Root of the yaml file */
    @Data
    public static class File {
        private List<ApiDefinition> apis = List.of();
    }
}
