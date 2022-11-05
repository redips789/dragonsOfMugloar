package com.dragons.service.strategies;

import com.dragons.client.DragonsOfMugloarApi;
import com.dragons.model.Item;
import com.dragons.model.MessageWithCategory;
import com.dragons.model.MissionDifficulty;
import com.dragons.service.PreparationStrategy;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class BuyOtherItem implements PreparationStrategy {

    private final DragonsOfMugloarApi dragonsOfMugloarApi;

    public BuyOtherItem(DragonsOfMugloarApi dragonsOfMugloarApi) {
        this.dragonsOfMugloarApi = dragonsOfMugloarApi;
    }

    @Override
    public boolean valid(Integer lives, MessageWithCategory messageWithCategory, String gameId, List<Item> affordableItems) {
        return (lives == 1 || MissionDifficulty.HARD == messageWithCategory.difficulty())
                && affordableItems.size() > 0;
    }

    @Override
    public void apply(String gameId, List<Item> affordableItems) {
        var cheapestItem = affordableItems.stream().min(Comparator.comparingInt(Item::cost)).orElseThrow();

        dragonsOfMugloarApi.purchaseShopItem(gameId, cheapestItem.id());
    }


}
