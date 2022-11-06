package com.dragons.service.strategies;

import com.dragons.model.GameState;
import com.dragons.model.Item;
import com.dragons.model.PurchaseItemResponse;

public class StrategiesHelper {

    public static Item selectItem(GameState gameState, int itemPrice) {
        return gameState.getItemsAvailableToBuy().stream()
                .filter(item -> item.cost() == itemPrice)
                .findFirst()
                .orElseThrow();
    }

    public static void updateGameState(GameState gameState, PurchaseItemResponse purchaseItemResponse, Item selectedItem) {
        gameState.setCurrentLives(purchaseItemResponse.lives());
        gameState.setAvailableGold(purchaseItemResponse.gold());
        gameState.getItemsAvailableToBuy().remove(selectedItem);
        gameState.getCurrentItems().add(selectedItem);
    }
}
