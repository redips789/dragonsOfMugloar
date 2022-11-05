package com.dragons.service.strategies;

import com.dragons.client.DragonsOfMugloarApi;
import com.dragons.model.GameState;
import com.dragons.model.Item;
import com.dragons.model.MessageWithCategory;
import com.dragons.model.MissionDifficulty;
import com.dragons.service.GamePlayHelper;
import com.dragons.service.PreparationStrategy;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class BuyOtherItem implements PreparationStrategy {

    private final Integer priority = 1;

    private final DragonsOfMugloarApi dragonsOfMugloarApi;

    public BuyOtherItem(DragonsOfMugloarApi dragonsOfMugloarApi) {
        this.dragonsOfMugloarApi = dragonsOfMugloarApi;
    }

    @Override
    public boolean valid(GameState gameState, MessageWithCategory messageWithCategory) {
        return (gameState.getCurrentLives() == 1 || MissionDifficulty.HARD == messageWithCategory.difficulty())
                && gameState.getItems().size() > 0;
    }

    @Override
    public void apply(GameState gameState) {
        var affordableItems = GamePlayHelper.getAffordableItems(gameState.getItems(), gameState.getAvailableGold());

        if (affordableItems.isEmpty()) {
            return;
        }

        var cheapestItem = affordableItems.stream()
                .min(Comparator.comparingInt(Item::cost))
                .orElseThrow();

        var purchaseItemResponse = dragonsOfMugloarApi.purchaseShopItem(gameState.getGameId(), cheapestItem.id());
        gameState.setCurrentLives(purchaseItemResponse.lives());
        gameState.setAvailableGold(purchaseItemResponse.gold());
        gameState.getItems().remove(cheapestItem);
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
