package ch.danielm.priceprediction.controller;

import ch.danielm.priceprediction.model.PredictionRequest;
import ch.danielm.priceprediction.model.PricePrediction;
import ch.danielm.priceprediction.model.User;
import ch.danielm.priceprediction.service.*;
import io.github.bucket4j.ConsumptionProbe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.InvalidKeyException;
import java.security.Principal;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/prediction")
public class PredictionController {

    @Autowired
    private UserService userService;

    @Autowired
    private SubscriptionPlanService subscriptionPlanService;

    @Autowired
    private AssetPriceService assetPriceService;

    @Autowired
    private TimeService timeService;

    @GetMapping(path = "/{symbol}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<PricePrediction> getPrediction(@PathVariable final String symbol, final Principal principal) {
        User user = null;
        try {
            user = userService.getUser(principal.getName());
        } catch (InvalidKeyException e) {
            return ResponseEntity.status(NOT_FOUND).build();
        }
        if (!subscriptionPlanService.verifySymbolLimit(user, symbol)) {
            return ResponseEntity.status(TOO_MANY_REQUESTS).build();
        }
        final ConsumptionProbe probe = subscriptionPlanService.resolveAndTryConsume(user);
        if (probe.isConsumed()) {
            subscriptionPlanService.storeRequest(new PredictionRequest(user.getId(), symbol, timeService.getCurrentInstant()));
            return ResponseEntity.ok(new PricePrediction(symbol, assetPriceService.getPricePredictionFor(symbol)));
        }
        final long timeUntilRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
        return ResponseEntity.status(TOO_MANY_REQUESTS)
                .header("X-Rate-Limit-Retry-After-Seconds", String.valueOf(timeUntilRefill))
                .build();
    }

}
