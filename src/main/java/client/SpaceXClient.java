package client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.Launch;
import dto.QueryResponse;
import dto.Rocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class SpaceXClient {
    private static final Logger log = LoggerFactory.getLogger(SpaceXClient.class);

    private static final String BASE = "https://api.spacexdata.com/v4";
    private final HttpClient http;
    private final ObjectMapper mapper;

    public SpaceXClient(ObjectMapper mapper) {
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.mapper = mapper;
    }

    public QueryResponse<Launch> queryLaunches(Map<String, Object> query, Map<String, Object> options)
            throws IOException, InterruptedException {

        String body = mapper.writeValueAsString(Map.of("query", query, "options", options));

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/launches/query"))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Accept", "application/json")
                .header("User-Agent", "ServletsSpaceX/1.0")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        return sendJsonWithRetry(req, new TypeReference<>() {}, "POST /launches/query");
    }

    public Launch getLaunchById(String id) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/launches/" + id))
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/json")
                .header("User-Agent", "ServletsSpaceX/1.0")
                .GET()
                .build();

        return sendJsonWithRetry(req, Launch.class, "GET /launches/{id}");
    }

    public Rocket getRocketById(String id) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/rockets/" + id))
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/json")
                .header("User-Agent", "ServletsSpaceX/1.0")
                .GET()
                .build();

        return sendJsonWithRetry(req, Rocket.class, "GET /rockets/{id}");
    }

    private <T> T sendJsonWithRetry(HttpRequest req, Class<T> clazz, String op)
            throws IOException, InterruptedException {
        String json = sendTextWithRetry(req, op);
        return mapper.readValue(json, clazz);
    }

    private <T> T sendJsonWithRetry(HttpRequest req, TypeReference<T> ref, String op)
            throws IOException, InterruptedException {
        String json = sendTextWithRetry(req, op);
        return mapper.readValue(json, ref);
    }

    private String sendTextWithRetry(HttpRequest req, String op) throws IOException, InterruptedException {
        IOException last = null;

        for (int attempt = 1; attempt <= 3; attempt++) {
            long t0 = System.nanoTime();
            try {
                log.debug("SpaceX -> {} attempt={} url={}", op, attempt, req.uri());
                HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                long ms = (System.nanoTime() - t0) / 1_000_000;

                log.info("SpaceX <- {} status={} ms={} url={}", op, resp.statusCode(), ms, req.uri());

                if (resp.statusCode() / 100 != 2) {
                    String b = resp.body();
                    String snippet = (b == null) ? "" : (b.length() <= 500 ? b : b.substring(0, 500) + "...");
                    throw new IOException("SpaceX API error: " + resp.statusCode() + ", body=" + snippet);
                }
                return resp.body();
            } catch (IOException e) {
                last = e;
                log.warn("SpaceX !! {} attempt={} url={} msg={}", op, attempt, req.uri(), e.toString());
                if (attempt < 3) Thread.sleep(250L * attempt);
            }
        }

        log.error("SpaceX FAIL {} url={}", op, req.uri(), last);
        throw last;
    }

    public List<Launch> getAllLaunches() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/launches"))
                .timeout(Duration.ofSeconds(90))
                .header("Accept", "application/json")
                .header("User-Agent", "ServletsSpaceX/1.0")
                .GET()
                .build();

        return sendJsonWithRetry(req, new TypeReference<List<Launch>>() {}, "GET /launches");
    }

}
