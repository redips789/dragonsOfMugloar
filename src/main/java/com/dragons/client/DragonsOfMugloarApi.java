package com.dragons.client;

import com.dragons.model.*;
import com.dragons.shared.HttpClientWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DragonsOfMugloarApi {

    private final HttpClientWrapper httpClient;

    private final String dragonsUri;

    public DragonsOfMugloarApi(HttpClientWrapper httpClient, @Value("${dragons_of_mugloar_app.host}") String dragonsUri) {
        this.httpClient = httpClient;
        this.dragonsUri = dragonsUri;
    }

    public StartGameResponse startGame() {
        return httpClient.post(StartGameResponse.class, dragonsUri, "game", "start");
    }

    public List<Message> getAllMessages(String gameId) {
        return httpClient.getList(Message[].class, dragonsUri, gameId, "messages");
    }

    public SolveMessageResponse solveMessage(String gameId, String messageId) {
        return httpClient.post(SolveMessageResponse.class, dragonsUri, gameId, "solve", messageId);
    }

    public List<Item> listAvailableItems(String gameId) {
        return httpClient.getList(Item[].class, dragonsUri, gameId, "shop");
    }

    public PurchaseItemResponse purchaseItem(String gameId, String itemId) {
        return httpClient.post(PurchaseItemResponse.class, dragonsUri, gameId, "shop", "buy", itemId);
    }
}
