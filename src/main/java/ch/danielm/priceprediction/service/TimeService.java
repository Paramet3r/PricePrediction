package ch.danielm.priceprediction.service;

import org.springframework.stereotype.Component;

import java.time.Instant;

import static java.time.Instant.now;

@Component
public class TimeService {

    public Instant getCurrentInstant() {
        return now();
    }
}
