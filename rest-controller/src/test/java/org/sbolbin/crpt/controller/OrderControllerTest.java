package org.sbolbin.crpt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.util.stream.Stream;

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

    @ParameterizedTest(name = "{index} Order validation failed - {1}")
    @DisplayName("Order validation failed")
    @MethodSource("validationFailedSource")
    public void test_validationFailed(Order payload, String responseBody) throws Exception {
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderToJson(payload)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().json(responseBody, true));
    }

    public static Stream<Arguments> validationFailedSource() {
        return Stream.of(
                Arguments.of(new Order(), "{\n" +
                        "    \"customer\": \"customer must be specified\",\n" +
                        "    \"products\": \"must not be empty\",\n" +
                        "    \"seller\": \"seller must be specified\"\n" +
                        "}"),
                Arguments.of(VALID.withSeller("123"), "{\n" +
                        "    \"seller\": \"seller must contain exactly 9 symbols\"\n" +
                        "}"),
                Arguments.of(VALID.withSeller("123456789tooLong"), "{\n" +
                        "    \"seller\": \"seller must contain exactly 9 symbols\"\n" +
                        "}"),
                Arguments.of(VALID.withCustomer("123456"), "{\n" +
                        "    \"customer\": \"customer must contain exactly 9 symbols\"\n" +
                        "}"),
                Arguments.of(VALID.withCustomer("123456789tooLong"), "{\n" +
                        "    \"customer\": \"customer must contain exactly 9 symbols\"\n" +
                        "}"),
                Arguments.of(VALID.withProducts(null), "{\n" +
                        "    \"products\": \"must not be empty\"\n" +
                        "}"),
                Arguments.of(VALID.withProducts(Collections.singletonList(new Product("shortCode", "milk"))), "{\n" +
                        "    \"products[0].code\": \"product code must contain exactly 13 symbols\"\n" +
                        "}"),
                Arguments.of(VALID.withProducts(Collections.singletonList(new Product("1234567890VeryLongCode", "milk"))), "{\n" +
                        "    \"products[0].code\": \"product code must contain exactly 13 symbols\"\n" +
                        "}"),
                Arguments.of(VALID.withProducts(Collections.singletonList(new Product("1234567890123", null))), "{\n" +
                        "    \"products[0].name\": \"product name must be specified\"\n" +
                        "}")
        );
    }

    @SneakyThrows
    private static String orderToJson(Order order) {
        return objectMapper.writeValueAsString(order);
    }
}