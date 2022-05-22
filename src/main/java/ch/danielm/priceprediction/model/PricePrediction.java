package ch.danielm.priceprediction.model;

import java.math.BigDecimal;

public class PricePrediction {

    private final String symbol;
    private final BigDecimal price;

    private final String ccy;
    public PricePrediction(final String symbol, final BigDecimal price) {
        this.symbol = symbol;
        this.price = price;
        this.ccy = "USD";
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCcy() {
        return ccy;
    }
}
