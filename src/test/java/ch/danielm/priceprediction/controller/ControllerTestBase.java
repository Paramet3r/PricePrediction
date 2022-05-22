package ch.danielm.priceprediction.controller;

import ch.danielm.priceprediction.NatWestPricePredictionApplication;
import ch.danielm.priceprediction.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NatWestPricePredictionApplication.class)
@WebAppConfiguration
public abstract class ControllerTestBase {

    protected MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                             .apply(springSecurity())
                             .build();
    }

    protected String mapToJson(final Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }

    protected <T> T mapFromJson(final String json, final Class<T> clazz)
            throws JsonParseException, IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.readValue(json, clazz);
    }

    protected User createUser(final String name, final String password) {
        final User user = new User();
        user.setName(name);
        user.setPassword(password);
        return user;
    }

    protected abstract String getURI();

    protected MvcResult updatePlan(final String userName, final String planName) throws Exception {
        return mvc.perform(put("/user/" + userName + "/plan")
                        .with(user(userName).password("test").roles("USER"))
                        .contentType(APPLICATION_JSON_VALUE)
                        .content("{\"plan\" : \"" + planName + "\"}")
                        .accept(APPLICATION_JSON_VALUE))
                .andReturn();
    }

    protected MvcResult register(final String name) throws Exception {
        return mvc.perform(post("/user/register")
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(mapToJson(createUser(name, "test")))
                        .accept(APPLICATION_JSON_VALUE))
                .andReturn();
    }
}
