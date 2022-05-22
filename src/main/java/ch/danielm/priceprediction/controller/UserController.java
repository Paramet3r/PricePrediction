package ch.danielm.priceprediction.controller;

import ch.danielm.priceprediction.model.SubscriptionPlan;
import ch.danielm.priceprediction.model.User;
import ch.danielm.priceprediction.service.SubscriptionPlanService;
import ch.danielm.priceprediction.service.TimeService;
import ch.danielm.priceprediction.service.UserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static ch.danielm.priceprediction.model.SubscriptionPlan.DEMO;
import static java.time.Duration.ofDays;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/user")
public class UserController {

    @Autowired
    private Environment env;
    @Autowired
    private UserService userService;
    @Autowired
    private TimeService timeService;

    @Autowired
    private SubscriptionPlanService subscriptionPlanService;
    @GetMapping(path = "/{name}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<User> getUserByName(@PathVariable final String name) {
        return ResponseEntity.of(userService.findUser(name));
    }

    @PostMapping(path = "/register", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<User> registerUser(@RequestBody final User user) {
        if (user.getName() == null || user.getPassword() == null) {
            return ResponseEntity.status(BAD_REQUEST)
                    .build();
        }
        if (userService.exists(user.getName())) {
            return ResponseEntity.status(CONFLICT)
                    .build();
        }
        if (user.getSubscriptionPlan() == null) {
            user.setSubscriptionPlan(DEMO);
            user.setLastPlanModificationTime(timeService.getCurrentInstant().minus(ofDays(31)));
        } else {
            user.setLastPlanModificationTime(timeService.getCurrentInstant());
        }
        final User createdUser = userService.persist(user);
        return ResponseEntity.status(CREATED)
                .body(createdUser);
    }

    @GetMapping(path = "/{name}/plan", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SubscriptionPlan> getPlanForUser(@PathVariable final String name) {
        return ResponseEntity.of(userService.findUser(name).map(User::getSubscriptionPlan));
    }

    @PutMapping(path = "/{name}/plan", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SubscriptionPlan> updatePlanForUser(@PathVariable final String name, @RequestBody String planChoice) {
        SubscriptionPlan plan;
        try {
            plan = SubscriptionPlan.valueOf(new JSONObject(planChoice).getString("plan"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(BAD_REQUEST)
                    .build();
        }
        final User user = userService.findUser(name).orElse(null);
        if (user == null) {
            return ResponseEntity.status(NOT_FOUND)
                    .build();
        }
        final Long planChangeFrequency = env.getProperty("subscriptionplan.change.frequencyDays", Long.class, 30L);
        if (user.getSubscriptionPlan().equals(plan)
                || user.getLastPlanModificationTime().plus(ofDays(planChangeFrequency)).isAfter(timeService.getCurrentInstant())) {
            return ResponseEntity.status(NOT_MODIFIED)
                    .build();
        }
        user.setSubscriptionPlan(plan);
        user.setLastPlanModificationTime(timeService.getCurrentInstant());
        subscriptionPlanService.updateForUser(user);
        userService.persist(user);
        return ResponseEntity.ok()
                             .body(plan);
    }
}
