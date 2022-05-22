package ch.danielm.priceprediction.service;

import ch.danielm.priceprediction.model.PredictionRequest;
import ch.danielm.priceprediction.model.User;
import ch.danielm.priceprediction.repository.PredictionRequestRepository;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope("singleton")
public class SubscriptionPlanService {

    @Autowired
    private PredictionRequestRepository requestRepository;
    private final Map<String, Bucket> bucketStore;

    public SubscriptionPlanService() {
        this.bucketStore = new ConcurrentHashMap<>();
    }

    public boolean verifySymbolLimit(final User user, final String symbol) {
        final Set<String> requestedSymbols = requestRepository.getSymbolsWithinDaysForUser(user.getId(), 30);
        return requestedSymbols.contains(symbol) || requestedSymbols.size() <= user.getSubscriptionPlan().getSymbolLimit();
    }

    public void storeRequest(final PredictionRequest predictionRequest) {
        requestRepository.save(predictionRequest);
    }

    public ConsumptionProbe resolveAndTryConsume(final User user) {
        Bucket bucket = bucketStore.get(user.getName());
        if (bucket == null) {
            bucket = bucketForUser(user);
            bucketStore.put(user.getName(), bucket);
        }
        return bucket.tryConsumeAndReturnRemaining(1);
    }

    public void updateForUser(final User user) {
        bucketStore.put(user.getName(), bucketForUser(user));
    }

    private Bucket bucketForUser(final User user) {
        return Bucket.builder()
                     .addLimit(user.getSubscriptionPlan().getBandwidth())
                     .build();
    }
}
