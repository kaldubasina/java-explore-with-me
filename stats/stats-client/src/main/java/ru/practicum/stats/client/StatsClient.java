package ru.practicum.stats.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.stats.common.dto.EndpointHit;
import ru.practicum.stats.common.dto.ViewStats;

import java.util.*;

@Service
public class StatsClient {

    @Value("${stats-server.url}")
    private String serverUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public void addStats(EndpointHit endpointHit) {
        restTemplate.postForLocation("/hit", endpointHit);
    }

    public List<ViewStats> getStats(String start, String end, List<String> uris, boolean unique) {
        Map<String, Object> parameters = new HashMap<>(Map.of(
                "start", start,
                "end", end,
                "unique", unique));

        if (uris != null && !uris.isEmpty()) {
            parameters.put("uris", String.join(",", uris));
        }

        ViewStats[] response = restTemplate.getForObject(
                "/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                ViewStats[].class, parameters);
        return Objects.isNull(response)
                ? Collections.emptyList()
                : List.of(response);
    }
}
