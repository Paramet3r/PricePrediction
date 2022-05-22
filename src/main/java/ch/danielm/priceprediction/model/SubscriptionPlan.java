package ch.danielm.priceprediction.model;

import io.github.bucket4j.Bandwidth;

import static io.github.bucket4j.Bandwidth.classic;
import static io.github.bucket4j.Refill.intervally;
import static java.time.Duration.*;

public enum SubscriptionPlan {
    DEMO(10, classic(1000, intervally(1000, ofDays(30L)))),
    SILVER(100, classic(1, intervally(1, ofMinutes(1L)))),
    GOLD(-1, classic(1, intervally(1, ofSeconds(10L))));

    private final long symbolLimit;

    private final Bandwidth bandwidth;
    SubscriptionPlan(final long symbolLimit, final Bandwidth bandwidth) {
        this.symbolLimit = symbolLimit;
        this.bandwidth = bandwidth;
    }

    public Bandwidth getBandwidth() {
        return bandwidth;
    }

    public long getSymbolLimit() {
        return symbolLimit;
    }
}
