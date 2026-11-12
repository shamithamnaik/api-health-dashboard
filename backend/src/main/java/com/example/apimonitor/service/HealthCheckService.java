package com.example.apimonitor.service;

import com.example.apimonitor.dto.CheckResultEvent;
import com.example.apimonitor.model.ApiEndpoint;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.Instant;

/** Calls one API (REST or SOAP) and turns the outcome into a CheckResultEvent. */
@Service
public class HealthCheckService {
    private final HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(5)).build();

    public CheckResultEvent check(ApiEndpoint api) {
        long start = System.currentTimeMillis();
        try {
            HttpRequest.Builder req = HttpRequest.newBuilder(URI.create(api.getUrl()))
                    .timeout(Duration.ofMillis(api.getTimeoutMs()))
                    .header("User-Agent", "api-monitor");
            boolean soap = "SOAP".equals(api.getType());
            String body = api.getBody() == null ? "" : api.getBody();

            if (soap) {
                req.header("Content-Type", "text/xml; charset=utf-8");
                if (api.getSoapAction() != null) req.header("SOAPAction", "\"" + api.getSoapAction() + "\"");
                req.POST(BodyPublishers.ofString(body));
            } else {
                req.method(api.getMethod(), body.isEmpty() ? BodyPublishers.noBody() : BodyPublishers.ofString(body));
            }

            HttpResponse<String> res = client.send(req.build(), BodyHandlers.ofString());
            long ms = System.currentTimeMillis() - start;
            boolean ok = res.statusCode() == api.getExpectedStatus();
            String msg = ok ? "OK" : "Expected HTTP " + api.getExpectedStatus() + " but got " + res.statusCode();
            if (ok && soap && res.body().toLowerCase().contains("faultcode")) {
                ok = false;
                msg = "SOAP fault in response";
            }
            return new CheckResultEvent(api.getId(), ok ? "UP" : "DOWN", res.statusCode(), ms, msg, Instant.now());
        } catch (Exception e) {
            long ms = System.currentTimeMillis() - start;
            String msg = e.getClass().getSimpleName() + ": " + e.getMessage();
            return new CheckResultEvent(api.getId(), "DOWN", null, ms, msg, Instant.now());
        }
    }
}
