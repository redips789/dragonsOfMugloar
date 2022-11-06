package com.dragons.service.strategies;

import com.dragons.client.DragonsOfMugloarApi;
import com.dragons.model.GameState;
import com.dragons.service.PreparationStrategy;
import org.springframework.stereotype.Component;

@Component
public class BuyExpensiveItem implements PreparationStrategy {

    private final Integer priority = 2;

    private final DragonsOfMugloarApi dragonsOfMugloarApi;

    public BuyExpensiveItem(DragonsOfMugloarApi dragonsOfMugloarApi) {
        this.dragonsOfMugloarApi = dragonsOfMugloarApi;
    }

    @Override
    public boolean valid(GameState gameState) {
        var items = gameState.getItemsAvailableToBuy().stream()
                .filter(item -> item.cost() >= 300)
                .toList();

        return items.size() > 0 && gameState.getAvailableGold() >= 300;
    }

    @Override
    public void apply(GameState gameState) {
        while (true) {
            var selectedItem = StrategiesHelper.selectItem(gameState, 300);
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
