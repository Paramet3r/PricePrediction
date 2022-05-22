package ch.danielm.priceprediction.repository;

import ch.danielm.priceprediction.model.PredictionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface PredictionRequestRepository extends JpaRepository<PredictionRequest, Long> {

    @Query(value = "SELECT DISTINCT symbol FROM predictionrequest WHERE userid = ?1 AND DATEDIFF('DAY', NOW(), requesttime) < ?2", nativeQuery = true)
    Set<String> getSymbolsWithinDaysForUser(final long userId, final long days);
}
