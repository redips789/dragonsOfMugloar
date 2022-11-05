package com.dragons.service.strategies;

import com.dragons.client.DragonsOfMugloarApi;
import com.dragons.model.GameState;
import com.dragons.model.Item;
import com.dragons.model.MessageWithCategory;
import com.dragons.model.PurchaseItemResponse;
import com.dragons.service.PreparationStrategy;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class BuyCheapItem implements PreparationStrategy {

    private final Integer priority = 1;

    private final DragonsOfMugloarApi dragonsOfMugloarApi;

    public BuyCheapItem(DragonsOfMugloarApi dragonsOfMugloarApi) {
        this.dragonsOfMugloarApi = dragonsOfMugloarApi;
    }

    @Override
    public boolean valid(GameState gameState, MessageWithCategory messageWithCategory) {
        var items = gameState.getItemsAvailableToBuy().stream()
                .filter(item -> item.cost() == 100)
                .toList();

        return items.size() > 0 && gameState.getAvailableGold() >= 100;
    }

    @Override
    public void apply(GameState gameState) {
        while (true) {
            var selectedItem = selectItem(gameState);
            var purchaseItemResponse = dragonsOfMugloarApi.purchaseShopItem(gameState.getGameId(), selectedItem.id());

            if (purchaseItemResponse.shoppingSuccess()) {
                updateGameState(gameState, purchaseItemResponse, selectedItem);
                break;
            }
        }
    }

    private Item selectItem(GameState gameState) {
        return gameState.getItemsAvailableToBuy().stream()
                .filter(item -> item.cost() == 100)
                .min(Comparator.comparingInt(Item::cost)).orElseThrow();
    }

    private void updateGameState(GameState gameState, PurchaseItemResponse purchaseItemResponse, Item selectedItem) {
        gameState.setCurrentLives(purchaseItemResponse.lives());
        gameState.setAvailableGold(purchaseItemResponse.gold());
        gameState.getItemsAvailableToBuy().remove(selectedItem);
        gameState.getCurrentItems().add(selectedItem);
    }

    @Override
    public Integer getPriority() {
        return priority;
    }

    @Override
    public int compareTo(PreparationStrategy o) {
        return priority - o.getPriority();
    }
}
