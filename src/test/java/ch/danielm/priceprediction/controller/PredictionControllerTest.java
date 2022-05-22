package ch.danielm.priceprediction.controller;

import ch.danielm.priceprediction.model.SubscriptionPlan;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static ch.danielm.priceprediction.model.SubscriptionPlan.DEMO;
import static ch.danielm.priceprediction.model.SubscriptionPlan.GOLD;
import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

public class PredictionControllerTest extends ControllerTestBase {

    private static final String uri = "/prediction";

    @Test
    public void testSimpleRequest() throws Exception {
        register("testSimpleRequest");
        final MvcResult mvcResult = requestPrediction("testSimpleRequest", "spy");
        assertEquals(OK.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void testThrottling() throws Exception {
        register("testThrottling");
        updatePlan("testThrottling", GOLD.name());

        final MvcResult firstResult = requestPrediction("testThrottling", "spy");
        final MvcResult secondResult = requestPrediction("testThrottling", "spy");

        assertEquals(TOO_MANY_REQUESTS.value(), firstResult.getResponse().getStatus());
        assertEquals(TOO_MANY_REQUESTS.value(), secondResult.getResponse().getStatus());
    }

    @Test
    public void testSymbolLimit() throws Exception {
        register("testSymbolLimit");
        for (int i = 0; i < DEMO.getSymbolLimit()+1; i++) {
            requestPrediction("testSymbolLimit", String.valueOf(i));
        }
        //give time to process previous requests
        sleep(1000);
        final MvcResult mvcResult = requestPrediction("testSymbolLimit", "spy");
        assertEquals(TOO_MANY_REQUESTS.value(), mvcResult.getResponse().getStatus());
    }

    private MvcResult requestPrediction(final String userName, final String symbol) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.get(uri + "/" + symbol)
                        .with(user(userName).password("test").roles("USER"))
                        .accept(APPLICATION_JSON_VALUE))
                .andReturn();
    }

    @Override
    protected String getURI() {
        return uri;
    }
}
