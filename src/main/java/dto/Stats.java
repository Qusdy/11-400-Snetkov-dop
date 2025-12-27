package dto;

import java.util.Map;

public class Stats {
    public final double successPct;
    public final int totalWithKnownResult;
    public final Map<String, Integer> launchesByYear;

    public Stats(double successPct, int totalWithKnownResult, Map<String, Integer> launchesByYear) {
        this.successPct = successPct;
        this.totalWithKnownResult = totalWithKnownResult;
        this.launchesByYear = launchesByYear;
    }

    public double getSuccessPct() {
        return successPct;
    }

    public int getTotalWithKnownResult() {
        return totalWithKnownResult;
    }

    public Map<String, Integer> getLaunchesByYear() {
        return launchesByYear;
    }
}
