package com.example.webclienttestdemo302;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatList;
import static org.assertj.core.api.MapAssert.assertThatMap;

import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "10000") // millis, 10 seconds
class WebTestClientWithServerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void webTestClient_works() throws JsonProcessingException {
        // given: request spec
        var requestSpe = webTestClient
                             .get()
                             .uri("https://dummyjson.com/products");

        // when: get request got OK response and response extracted
        var response = requestSpe.exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

        // and : JSON response parsed into Map
        String itemsString = new ObjectMapper().writerWithDefaultPrettyPrinter()
                     .writeValueAsString(response);
        Map<String, Object> itemsMap = new ObjectMapper().readValue(response, Map.class);

        // then: verify response elements
        assertThat(itemsMap).size().isEqualTo(4);
        assertThatMap(itemsMap).containsKey("products");
        assertThatMap(itemsMap)
            .extracting("products")
            .isInstanceOf(List.class);

        var productsList = (List)itemsMap.get("products");

        assertThatList(productsList).size().isEqualTo(30);
        assertThat(
            ((Map)productsList.get(0)).get("title")
        ).isEqualTo("iPhone 9");
    }
}
