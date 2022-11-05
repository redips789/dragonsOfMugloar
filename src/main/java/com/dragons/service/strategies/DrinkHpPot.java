package com.dragons.service.strategies;

import com.dragons.model.MessageWithCategory;
import com.dragons.service.PreparationStrategy;
import org.springframework.stereotype.Component;

@Component
public class DrinkHpPot implements PreparationStrategy {

    @Override
    public boolean valid(Integer lives, MessageWithCategory messageWithCategory, String gameId, Integer availableGold) {
        return false;
    }

    @Override
    public void apply(String gameId) {

    }
}
