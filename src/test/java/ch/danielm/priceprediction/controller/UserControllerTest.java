package ch.danielm.priceprediction.controller;

import ch.danielm.priceprediction.model.SubscriptionPlan;
import ch.danielm.priceprediction.model.User;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static ch.danielm.priceprediction.model.SubscriptionPlan.DEMO;
import static ch.danielm.priceprediction.model.SubscriptionPlan.SILVER;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

public class UserControllerTest extends ControllerTestBase {

    private static final String uri = "/user";

    @Test
    public void testRegister() throws Exception {
        final MvcResult mvcResult = register("daniel1");

        assertEquals(CREATED.value(), mvcResult.getResponse().getStatus());
        String content = mvcResult.getResponse().getContentAsString();
        User user = super.mapFromJson(content, User.class);
        assertEquals("daniel1", user.getName());
    }

    @Test
    public void testRegisterNameConflict() throws Exception {
        register("daniel2");

        final MvcResult errorResult = register("daniel2");
        assertEquals(CONFLICT.value(), errorResult.getResponse().getStatus());
    }

    @Test
    public void testGetUser() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri + "/testGetUser")
                                          .with(user("testGetUser").password("test").roles("USER"))
                                          .accept(APPLICATION_JSON_VALUE))
                                 .andReturn();
        assertEquals(NOT_FOUND.value(), mvcResult.getResponse().getStatus());

        register("testGetUser");

        mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri + "/testGetUser")
                                .with(user("testGetUser").password("test").roles("USER"))
                                .accept(APPLICATION_JSON_VALUE))
                       .andReturn();
        assertEquals(OK.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void testGetPlan() throws Exception {
        register("testGetPlan");
        final MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri+"/testGetPlan/plan")
                        .with(user("testGetPlan").password("test").roles("USER"))
                        .accept(APPLICATION_JSON_VALUE))
                .andReturn();
        assertEquals(OK.value(), mvcResult.getResponse().getStatus());
        final SubscriptionPlan subscriptionPlan = super.mapFromJson(mvcResult.getResponse().getContentAsString(), SubscriptionPlan.class);
        assertEquals(DEMO, subscriptionPlan);
    }

    @Test
    public void testChangePlan() throws Exception {
        register("testChangePlan");
        final MvcResult mvcResult = updatePlan("testChangePlan", "SILVER");

        assertEquals(OK.value(), mvcResult.getResponse().getStatus());
        final SubscriptionPlan subscriptionPlan = super.mapFromJson(mvcResult.getResponse().getContentAsString(), SubscriptionPlan.class);
        assertEquals(SILVER, subscriptionPlan);

        final MvcResult errorResult = updatePlan("testChangePlan", "GOLD");
        assertEquals(NOT_MODIFIED.value(), errorResult.getResponse().getStatus());
    }

    @Test
    public void testInvalidPlan() throws Exception {
        register("testInvalidPlan");
        final MvcResult errorResult = updatePlan("testInvalidPlan", "PLATINUM");
        assertEquals(BAD_REQUEST.value(), errorResult.getResponse().getStatus());
    }

    @Override
    protected String getURI() {
        return uri;
    }
}
