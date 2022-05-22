package ch.danielm.priceprediction.service;

import java.math.BigDecimal;

public interface AssetPriceService {

    BigDecimal getPricePredictionFor(final String symbol);
}
