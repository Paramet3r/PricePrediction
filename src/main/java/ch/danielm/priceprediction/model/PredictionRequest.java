package ch.danielm.priceprediction.model;

import javax.persistence.*;

import java.time.Instant;

import static javax.persistence.GenerationType.AUTO;

@Entity
@Table(name = "predictionrequest")
public class PredictionRequest {

    @Id
    @GeneratedValue(strategy = AUTO)
    private long id;
    @Column(name = "userid")
    private final long userId;
    @Column(name = "symbol")
    private final String symbol;
    @Column(name = "requesttime")
    private final Instant requestTime;

    public PredictionRequest(long userId, String symbol, Instant requestTime) {
        this.userId = userId;
        this.symbol = symbol;
        this.requestTime = requestTime;
    }

    public long getUserId() {
        return userId;
    }

    public String getSymbol() {
        return symbol;
    }

    public Instant getRequestTime() {
        return requestTime;
    }
}
