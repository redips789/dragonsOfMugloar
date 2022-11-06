package com.dragons.service.strategies;

import com.dragons.client.DragonsOfMugloarApi;
import com.dragons.model.GameState;
import com.dragons.service.PreparationStrategy;
import org.springframework.stereotype.Component;

@Component
public class BuyCheapItem implements PreparationStrategy {

    private final Integer priority = 1;

    private final DragonsOfMugloarApi dragonsOfMugloarApi;

    public BuyCheapItem(DragonsOfMugloarApi dragonsOfMugloarApi) {
        this.dragonsOfMugloarApi = dragonsOfMugloarApi;
    }

    @Override
    public boolean valid(GameState gameState) {
        var items = gameState.getItemsAvailableToBuy().stream()
                .filter(item -> item.cost() == 100)
                .toList();

        return items.size() > 0 && gameState.getAvailableGold() >= 100;
    }

    @Override
    public void apply(GameState gameState) {
        while (true) {
            var selectedItem = StrategiesHelper.selectItem(gameState, 100);
            var purchaseItemResponse = dragonsOfMugloarApi.purchaseItem(gameState.getGameId(), selectedItem.id());

            if (purchaseItemResponse.shoppingSuccess()) {
                StrategiesHelper.updateGameState(gameState, purchaseItemResponse, selectedItem);
                break;
            }
        }
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
