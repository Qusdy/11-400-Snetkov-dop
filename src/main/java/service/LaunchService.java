package service;

import client.SpaceXClient;
import dto.Launch;
import dto.Rocket;
import dto.Stats;

import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;

public class LaunchService {
    private final SpaceXClient client;

    public LaunchService(SpaceXClient client) {
        this.client = client;
    }

    public List<Launch> loadAllLaunches() throws IOException, InterruptedException {
        return client.getAllLaunches();
    }

    public Map<String, String> loadRocketMap() throws IOException, InterruptedException {
        List<Rocket> rockets = client.getAllRockets();
        Map<String, String> map = new HashMap<>();
        for (Rocket r : rockets) {
            if (r != null && r.id != null) map.put(r.id, r.name);
        }
        return map;
    }

    public void enrichRocketNames(List<Launch> launches, Map<String, String> rocketIdToName) {
        if (launches == null || rocketIdToName == null) return;
        for (Launch l : launches) {
            if (l == null) continue;
            l.rocketName = rocketIdToName.get(l.rocket);
        }
    }

    public List<Launch> filter(List<Launch> all, Integer year, String status, String rocketName, String q) {
        if (all == null) return List.of();

        String st = (status == null || status.isBlank()) ? "all" : status.trim().toLowerCase(Locale.ROOT);
        String rocket = (rocketName == null) ? "" : rocketName.trim().toLowerCase(Locale.ROOT);
        String query = (q == null) ? "" : q.trim().toLowerCase(Locale.ROOT);

        List<Launch> out = new ArrayList<>();
        for (Launch l : all) {
            if (l == null) continue;

            if (!matchesYear(l, year)) continue;
            if (!matchesStatus(l, st)) continue;

            if (!rocket.isEmpty()) {
                String rn = safe(l.rocketName).toLowerCase(Locale.ROOT);
                if (!rn.equals(rocket)) continue; // Falcon 9 == Falcon 9
            }

            if (!query.isEmpty()) {
                String name = safe(l.name).toLowerCase(Locale.ROOT);
                if (!name.contains(query)) continue;
            }

            out.add(l);
        }
        return out;
    }

    public List<Launch> lastN(List<Launch> list, int n) {
        if (list == null) return List.of();
        list.sort((a, b) -> parseInstantSafe(b.dateUtc).compareTo(parseInstantSafe(a.dateUtc)));
        if (n < 0) n = 0;
        if (list.size() <= n) return list;
        return new ArrayList<>(list.subList(0, n));
    }

    public Launch findById(List<Launch> all, String id) {
        if (all == null || id == null) return null;
        for (Launch l : all) {
            if (l != null && id.equals(l.id)) return l;
        }
        return null;
    }

    public Optional<Launch> nextUpcoming(List<Launch> all) {
        if (all == null) return Optional.empty();

        Launch best = null;
        Instant bestTs = null;

        for (Launch l : all) {
            if (l == null) continue;
            if (!Boolean.TRUE.equals(l.upcoming)) continue;
            if (l.dateUtc == null) continue;

            Instant ts = parseInstantSafe(l.dateUtc);
            if (best == null || ts.isBefore(bestTs)) {
                best = l;
                bestTs = ts;
            }
        }
        return Optional.ofNullable(best);
    }

    public List<Launch> timeline(List<Launch> all) {
        if (all == null) return List.of();
        List<Launch> list = new ArrayList<>(all);
        list.sort(Comparator.comparing(l -> parseInstantSafe(l.dateUtc)));
        return list;
    }

    public Stats stats(List<Launch> all) {
        if (all == null) return new Stats(0.0, 0, new TreeMap<>());

        int known = 0;
        int ok = 0;
        Map<String, Integer> byYear = new TreeMap<>();

        for (Launch l : all) {
            if (l == null) continue;

            Integer y = yearFromUtc(l.dateUtc);
            if (y != null) byYear.merge(String.valueOf(y), 1, Integer::sum);

            if (l.success != null) {
                known++;
                if (Boolean.TRUE.equals(l.success)) ok++;
            }
        }

        double pct = (known == 0) ? 0.0 : (100.0 * ok / known);
        return new Stats(pct, known, byYear);
    }

    private boolean matchesStatus(Launch l, String status) {
        if ("all".equals(status)) return true;
        if ("success".equals(status)) return Boolean.TRUE.equals(l.success);
        if ("failure".equals(status)) return Boolean.FALSE.equals(l.success);
        return true;
    }

    private boolean matchesYear(Launch l, Integer year) {
        if (year == null) return true;
        Integer y = yearFromUtc(l.dateUtc);
        return y != null && y.equals(year);
    }

    private Integer yearFromUtc(String dateUtc) {
        if (dateUtc == null) return null;
        try {
            return OffsetDateTime.parse(dateUtc).getYear();
        } catch (Exception e) {
            return null;
        }
    }

    private Instant parseInstantSafe(String dateUtc) {
        if (dateUtc == null) return Instant.EPOCH;
        try {
            return Instant.parse(dateUtc);
        } catch (Exception e) {
            return Instant.EPOCH;
        }
    }

    private String safe(String s) {
        return (s == null) ? "" : s;
    }
}