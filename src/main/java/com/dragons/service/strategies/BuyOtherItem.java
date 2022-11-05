package com.dragons.service.strategies;

import com.dragons.client.DragonsOfMugloarApi;
import com.dragons.model.GameState;
import com.dragons.model.Item;
import com.dragons.model.MessageWithCategory;
import com.dragons.model.MissionDifficulty;
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
                && gameState.getAffordableItems().size() > 0;
    }

    @Override
    public void apply(GameState gameState) {
        var cheapestItem = gameState.getAffordableItems().stream().min(Comparator.comparingInt(Item::cost)).orElseThrow();

        dragonsOfMugloarApi.purchaseShopItem(gameState.getGameId(), cheapestItem.id());
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
