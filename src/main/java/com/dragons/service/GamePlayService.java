package com.dragons.service;

import com.dragons.client.DragonsOfMugloarApi;
import com.dragons.model.Item;
import com.dragons.model.Message;
import com.dragons.model.MessageWithCategory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GamePlayService {

    public static final String HEALTH_POT_ID = "hpot";
    private final DragonsOfMugloarApi dragonsOfMugloarApi;
    private final MissionPreparatory missionPreparatory;
    private final List<PreparationStrategy> preparationStrategies;

    public GamePlayService(DragonsOfMugloarApi dragonsOfMugloarApi, MissionPreparatory missionPreparatory,
                           List<PreparationStrategy> preparationStrategies) {
        this.dragonsOfMugloarApi = dragonsOfMugloarApi;
        this.missionPreparatory = missionPreparatory;
        this.preparationStrategies = preparationStrategies;
    }

    public Integer play() {
        var gameState = dragonsOfMugloarApi.startGame();
        var gameId = gameState.gameId();
        var currentScore = 0;
        while (gameState.lives() > 0) {
            var solutionResponse = dragonsOfMugloarApi.solveMessage(gameId, getMessageId(gameId));
            currentScore = solutionResponse.score();

            if (solutionResponse.lives() == 0) {
                break;
            }

            if (solutionResponse.lives() == 1) {
                var items = dragonsOfMugloarApi.listItemsAvailableInShop(gameId);
                var affordableItems = items.stream()
                        .filter(item -> item.cost() < solutionResponse.gold())
                        .toList();

                var availableHpPot = getHealthPotId(affordableItems);

                if (availableHpPot.isPresent()) {
                    dragonsOfMugloarApi.purchaseShopItem(gameId, availableHpPot.orElseThrow());
                } else if (affordableItems.size() > 0) {
                    dragonsOfMugloarApi.purchaseShopItem(gameId, affordableItems.stream().findAny().orElseThrow().id());
                }
            }
        }
        System.out.println("Final score: " + currentScore);

        return currentScore;
    }

    private String getMessageId(String gameId) {
        var messages = dragonsOfMugloarApi.getMessages(gameId);
        var messagesWithGroup = messages.stream()
                .map(Message::mapTo)
                .collect(Collectors.toList());

        Comparator<MessageWithCategory> messageComparator = Comparator.comparing(MessageWithCategory::difficulty)
                .thenComparing(Comparator.comparingInt(MessageWithCategory::reward)
                        .reversed());

        messagesWithGroup.sort(messageComparator);

        return messagesWithGroup.stream()
                .findFirst()
                .orElseThrow()
                .adId();
    }

    private Optional<String> getHealthPotId(List<Item> itemList) {
        return itemList.stream()
                .filter(item -> item.id().equals(HEALTH_POT_ID))
                .sorted(Comparator.comparingInt(Item::cost))
                .map(Item::id)
                .findFirst();
    }
}
