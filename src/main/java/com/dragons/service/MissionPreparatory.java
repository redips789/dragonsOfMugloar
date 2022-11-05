package com.dragons.service;

import com.dragons.model.Item;
import com.dragons.model.MessageWithCategory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MissionPreparatory {

    public void prepare(Integer lives, MessageWithCategory messageWithCategory, String gameId,
                        List<PreparationStrategy> strategyList, List<Item> affordableItems) {
        strategyList.stream()
                .filter(strategy -> strategy.valid(lives, messageWithCategory, gameId, affordableItems))
                .forEach(strategy -> strategy.apply(gameId, affordableItems));
    }
}
