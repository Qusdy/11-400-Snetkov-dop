package client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.Launch;
import dto.Rocket;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

public class SpaceXClient {
    private static final String BASE = "https://api.spacexdata.com/v4";

    private final HttpClient http;
    private final ObjectMapper mapper;

    public SpaceXClient(ObjectMapper mapper) {
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.mapper = mapper;
    }

    public List<Launch> getAllLaunches() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/launches"))
                .timeout(Duration.ofSeconds(90))
                .header("Accept", "application/json")
                .header("User-Agent", "ServletsSpaceX/1.0")
                .GET()
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() / 100 != 2) throw new IOException("SpaceX API error: " + resp.statusCode());
        return mapper.readValue(resp.body(), new TypeReference<List<Launch>>() {});
    }

    public List<Rocket> getAllRockets() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/rockets"))
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/json")
                .header("User-Agent", "ServletsSpaceX/1.0")
                .GET()
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() / 100 != 2) throw new IOException("SpaceX API error: " + resp.statusCode());
        return mapper.readValue(resp.body(), new TypeReference<List<Rocket>>() {});
    }
}
