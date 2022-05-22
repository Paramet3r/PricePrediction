package ch.danielm.priceprediction.service;

import org.json.JSONArray;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.math.RoundingMode.HALF_DOWN;

@Component
public class AssetPriceSimpleService implements AssetPriceService {

    public BigDecimal getPricePredictionFor(final String symbol) {
        try {
            final URL url = new URL("http://www.randomnumberapi.com/api/v1.0/random?min=10&max=1000&count=1");
            final HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            return applyPrediction(extractResponseValue(con));
        } catch (IOException e) {
            throw new RequestRejectedException("Failed to retrieve price prediction");
        }
    }

    private BigDecimal extractResponseValue(final HttpURLConnection connection) throws IOException {
        BufferedReader br;
        if (100 <= connection.getResponseCode() && connection.getResponseCode() <= 399) {
            br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }
        final StringBuilder sb = new StringBuilder();
        String currentLine;
        while ((currentLine = br.readLine()) != null) {
            sb.append(currentLine);
        }
        return BigDecimal.valueOf(new JSONArray(sb.toString()).getLong(0));
    }

    private BigDecimal applyPrediction(final BigDecimal currentPrice) {
        final BigDecimal min = BigDecimal.valueOf(-100L);
        final BigDecimal max = BigDecimal.valueOf(100L);
        final BigDecimal randomBigDecimal = min.add(BigDecimal.valueOf(Math.random()).multiply(max.subtract(min)));
        return currentPrice.add(randomBigDecimal.setScale(2, HALF_DOWN));
    }
}
