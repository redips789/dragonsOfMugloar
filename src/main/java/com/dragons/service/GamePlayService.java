package com.dragons.service;

import com.dragons.client.DragonsOfMugloarApi;
import com.dragons.model.Item;
import com.dragons.model.Message;
import com.dragons.model.MessageWithCategory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GamePlayService {


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
        var currentScore = gameState.score();
        var currentLives = gameState.lives();
        var availableGold = gameState.gold();

        while (gameState.lives() > 0) {
            var messageWithCategory = getMessageWithCategory(gameId);
            var affordableItems = getAffordableItems(gameId, availableGold);
            missionPreparatory.prepare(currentLives, messageWithCategory, gameId, preparationStrategies, affordableItems);
            var solutionResponse = dragonsOfMugloarApi.solveMessage(gameId, messageWithCategory.adId());
            currentScore = solutionResponse.score();
            currentLives = solutionResponse.lives();
            availableGold = solutionResponse.gold();

            if (solutionResponse.lives() == 0) {
                break;
            }

        }

        return currentScore;
    }

    private MessageWithCategory getMessageWithCategory(String gameId) {
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
                .orElseThrow();
    }

    private List<Item> getAffordableItems(String gameId, Integer availableGold) {
        var items = dragonsOfMugloarApi.listItemsAvailableInShop(gameId);
        return items.stream()
                .filter(item -> item.cost() < availableGold)
                .toList();
    }
}
