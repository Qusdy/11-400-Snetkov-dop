package service;

import client.SpaceXClient;
import dto.Launch;
import dto.QueryResponse;

import java.io.IOException;
import java.util.*;

public class LaunchService {
    private final SpaceXClient client;

    public LaunchService(SpaceXClient client) {
        this.client = client;
    }

    public QueryResponse<Launch> lastLaunches(int limit,
                                              Integer year,
                                              String status,
                                              String rocketName,
                                              String search) throws IOException, InterruptedException {

        Map<String, Object> query = new LinkedHashMap<>();

        query.put("upcoming", false);

        if (year != null) {
            query.put("date_utc", Map.of("$regex", "^" + year + "-"));
        }

        if (status != null && !status.isBlank() && !"all".equalsIgnoreCase(status)) {
            if ("success".equalsIgnoreCase(status)) query.put("success", true);
            if ("failure".equalsIgnoreCase(status)) query.put("success", false);
        }

        if (search != null && !search.isBlank()) {
            query.put("name", Map.of("$regex", search, "$options", "i"));
        }

        Map<String, Object> options = new LinkedHashMap<>();
        options.put("limit", limit);
//        options.put("sort", Map.of("date_utc", "desc"));

        Map<String, Object> rocketPopulate = new LinkedHashMap<>();
        rocketPopulate.put("path", "rocket");
        rocketPopulate.put("select", Map.of("name", 1));

        if (rocketName != null && !rocketName.isBlank()) {
            rocketPopulate.put("match", Map.of("name", rocketName));
        }

        options.put("populate", List.of(rocketPopulate));

        QueryResponse<Launch> resp = client.queryLaunches(query, options);

        if (rocketName != null && !rocketName.isBlank() && resp.docs != null) {
            resp.docs.removeIf(l -> l == null || l.rocket == null);
        }

        return resp;
    }

    public Launch launchDetail(String id) throws IOException, InterruptedException {
        return client.getLaunchById(id);
    }

    public Optional<Launch> nextUpcoming() throws IOException, InterruptedException {
        Map<String, Object> query = Map.of("upcoming", true);

        Map<String, Object> options = new LinkedHashMap<>();
        options.put("limit", 1);
        options.put("sort", Map.of("date_utc", "asc"));

        QueryResponse<Launch> resp = client.queryLaunches(query, options);
        return resp.docs == null || resp.docs.isEmpty() ? Optional.empty() : Optional.of(resp.docs.get(0));
    }
}
