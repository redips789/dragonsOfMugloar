package com.dragons.api;

import com.dragons.utils.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
public class PlayEndPointTest {

    @Value("${dragons-of-mugloar.endpoints.base}")
    private String URI;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void playGame() {
        var response = this.restTemplate.postForEntity(this.URI, null, Integer.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(32);
    }

    //TODO
    @Test
    void playGame__withHpPotBuy() {

    }

    @Test
    void playGame__withCheapItemBuy() {

    }

    @Test
    void playGame__withExpensiveItemBuy() {

    }

    @Test
    void playGame__ItemOutOfStock() {

    }
}
