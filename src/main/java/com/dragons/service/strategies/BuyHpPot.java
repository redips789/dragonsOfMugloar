package com.dragons.service.strategies;

import com.dragons.client.DragonsOfMugloarApi;
import com.dragons.model.GameState;
import com.dragons.model.Item;
import com.dragons.model.MessageWithCategory;
import com.dragons.service.GamePlayHelper;
import com.dragons.service.PreparationStrategy;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class BuyHpPot implements PreparationStrategy {

    public static final String HEALTH_POT_ID = "hpot";

    public final Integer priority = 0;

    private final DragonsOfMugloarApi dragonsOfMugloarApi;

    public BuyHpPot(DragonsOfMugloarApi dragonsOfMugloarApi) {
        this.dragonsOfMugloarApi = dragonsOfMugloarApi;
    }

    @Override
    public boolean valid(GameState gameState, MessageWithCategory messageWithCategory) {
        return gameState.getCurrentLives() == 1 &&
                getHealthPot(gameState.getItems()).isPresent();
    }

    @Override
    public void apply(GameState gameState) {
        var affordableItems = GamePlayHelper.getAffordableItems(gameState.getItems(), gameState.getAvailableGold());

        if (affordableItems.isEmpty()) {
            return;
        }

        var healthPot = getHealthPot(affordableItems).orElseThrow();
        var purchaseItemResponse = dragonsOfMugloarApi.purchaseShopItem(gameState.getGameId(), healthPot.id());
        gameState.setCurrentLives(purchaseItemResponse.lives());
        gameState.setAvailableGold(purchaseItemResponse.gold());
        gameState.getItems().remove(healthPot);
    }

    private Optional<Item> getHealthPot(List<Item> itemList) {
        return itemList.stream()
                .filter(item -> item.id().equals(HEALTH_POT_ID))
                .sorted(Comparator.comparingInt(Item::cost))
                .findFirst();
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
