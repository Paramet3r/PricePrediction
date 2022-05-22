package ch.danielm.priceprediction.model;

import javax.persistence.*;

import java.time.Instant;
import java.util.Objects;

import static java.util.Objects.hash;
import static javax.persistence.GenerationType.AUTO;

@Entity
@Table(name = "_user")
public class User {

    @Id
    @GeneratedValue(strategy = AUTO)
    private long id;
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "subscriptionPlan")
    private SubscriptionPlan subscriptionPlan;
    @Column(name = "lastPlanModificationTime")
    private Instant lastPlanModificationTime;

    public long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }

    public void setSubscriptionPlan(final SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }

    public SubscriptionPlan getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public void setLastPlanModificationTime(final Instant lastPlanModificationTime) {
        this.lastPlanModificationTime = lastPlanModificationTime;
    }

    public Instant getLastPlanModificationTime() {
        return lastPlanModificationTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id
                && name.equals(user.name)
                && subscriptionPlan == user.subscriptionPlan
                && Objects.equals(lastPlanModificationTime, user.lastPlanModificationTime);
    }

    @Override
    public int hashCode() {
        return hash(id,
                    name,
                    subscriptionPlan,
                    lastPlanModificationTime);
    }
}
