package com.dragons.api;

import com.dragons.client.DragonsOfMugloarApi;
import com.dragons.fixture.FixtureFactory;
import com.dragons.utils.IntegrationTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;


@IntegrationTest
public class DragonsOfMugloarApiTest {
    private static final HttpHeaders HEADERS = new HttpHeaders();

    @Autowired
    private DragonsOfMugloarApi dragonsOfMugloarApi;

    @Value("${dragons_of_mugloar_app.host}")
    private String URI;

    @BeforeAll
    public static void setHttpHeaders() {
        HEADERS.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    void startGame() {
        var startGameResponse = dragonsOfMugloarApi.startGame();

        assertThat(startGameResponse.gameId()).isEqualTo("Rx4DpGC4");
        assertThat(startGameResponse.gold()).isEqualTo(0);
        assertThat(startGameResponse.lives()).isEqualTo(3);
        assertThat(startGameResponse.score()).isEqualTo(0);
        assertThat(startGameResponse.highScore()).isEqualTo(0);
        assertThat(startGameResponse.turn()).isEqualTo(0);
        assertThat(startGameResponse.level()).isEqualTo(0);
    }

    @Test
    void listMessages() {
        var messages = dragonsOfMugloarApi.getMessages("Rx4DpGC4");

        assertThat(messages).hasSize(10);
        assertThat(messages).containsAll(FixtureFactory.getMessages());
    }

    @Test
    void solveMessage() {
        var solveMessageResponse = dragonsOfMugloarApi.solveMessage("Rx4DpGC4", "YbQYYtut");

        assertThat(solveMessageResponse.success()).isTrue();
        assertThat(solveMessageResponse.gold()).isEqualTo(21);
        assertThat(solveMessageResponse.lives()).isEqualTo(3);
        assertThat(solveMessageResponse.score()).isEqualTo(3);
        assertThat(solveMessageResponse.turn()).isEqualTo(2);
    }

    @Test
    void listItemsAvailableShop() {
        var items = dragonsOfMugloarApi.listItemsAvailableInShop("Rx4DpGC4");

        assertThat(items).hasSize(11);
        assertThat(items).containsAll(FixtureFactory.getItems());
    }

    @Test
    void purchaseShopItem() {
        var purchaseItemResponse = dragonsOfMugloarApi.purchaseShopItem("Rx4DpGC4", "gas");

        assertThat(purchaseItemResponse.shoppingSuccess()).isFalse();
        assertThat(purchaseItemResponse.gold()).isEqualTo(0);
        assertThat(purchaseItemResponse.lives()).isEqualTo(3);
        assertThat(purchaseItemResponse.level()).isEqualTo(0);
        assertThat(purchaseItemResponse.turn()).isEqualTo(1);
    }
}
