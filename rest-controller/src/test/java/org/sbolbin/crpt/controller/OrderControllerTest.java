package org.sbolbin.crpt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sbolbin.crpt.domain.Order;
import org.sbolbin.crpt.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "classpath:spring-servlet.xml")
@WebAppConfiguration
class OrderControllerTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String PATH = "/v1/order";
    private static Order VALID;
    private static String VALID_PAYLOAD;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeAll
    public static void beforeAll() {
        VALID = new Order("123456789", "234567890",
                Collections.singletonList(new Product("1234567890123", "milk")));

        VALID_PAYLOAD = orderToJson(VALID);
    }

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    @DisplayName("unknown path requested - not found returned")
    public void test_invalidPath() throws Exception {
        mockMvc.perform(post("/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PAYLOAD))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"message\":\"Not found\"}", true));
    }

    @Test
    @DisplayName("unhandled method requested - not found returned")
    public void test_wrongMethod() throws Exception {
        mockMvc.perform(put(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PAYLOAD))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"message\":\"Not found\"}", true));
    }

    @Test
    @DisplayName("malformed body requested - 400 returned")
    public void test_invalidBody() throws Exception {
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("something wrong"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().json("{\"message\":\"The request body is not a valid JSON\"}", true));
    }

    @Test
    @DisplayName("unexpected content type requested - 400 returned")
    public void test_unexpectedContentType() throws Exception {
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_PDF)
                        .content(VALID_PAYLOAD))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().json("{\"message\":  \"Unsupported Content-Type\"}"));
    }

    @Test
    @DisplayName("valid order requested - validation passed")
    public void test_successfullyPassed() throws Exception {
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PAYLOAD))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    private static String orderToJson(Order order) {
        return objectMapper.writeValueAsString(order);
    }
}