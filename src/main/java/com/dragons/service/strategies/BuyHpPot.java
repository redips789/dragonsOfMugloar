package com.dragons.service.strategies;

import com.dragons.client.DragonsOfMugloarApi;
import com.dragons.model.Item;
import com.dragons.model.MessageWithCategory;
import com.dragons.service.PreparationStrategy;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class BuyHpPot implements PreparationStrategy {

    public static final String HEALTH_POT_ID = "hpot";

    private final DragonsOfMugloarApi dragonsOfMugloarApi;

    public BuyHpPot(DragonsOfMugloarApi dragonsOfMugloarApi) {
        this.dragonsOfMugloarApi = dragonsOfMugloarApi;
    }

    @Override
    public boolean valid(Integer lives, MessageWithCategory messageWithCategory, String gameId, List<Item> affordableItems) {
        return lives == 1 &&
                getHealthPotId(affordableItems).isPresent();
    }

    @Override
    public void apply(String gameId, List<Item> affordableItems) {
        dragonsOfMugloarApi.purchaseShopItem(gameId, getHealthPotId(affordableItems).orElseThrow());
    }

    private Optional<String> getHealthPotId(List<Item> itemList) {
        return itemList.stream()
                .filter(item -> item.id().equals(HEALTH_POT_ID))
                .sorted(Comparator.comparingInt(Item::cost))
                .map(Item::id)
                .findFirst();
    }
}
