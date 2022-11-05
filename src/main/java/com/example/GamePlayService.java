package com.example;

import com.example.client.DragonsOfMugloarApi;
import com.example.rest.Message;
import com.example.rest.MessageWithCategory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class GamePlayService {

    public static final String HEALTH_POT_ID = "hpot";
    private DragonsOfMugloarApi dragonsOfMugloarApi;

    public GamePlayService(DragonsOfMugloarApi dragonsOfMugloarApi) {
        this.dragonsOfMugloarApi = dragonsOfMugloarApi;
    }

    public Integer play() {
        var gameState = dragonsOfMugloarApi.startGame();
        var gameId = gameState.gameId();
        var currentScore = 0;
        while (gameState.lives() > 0) {
            var solutionResponse = dragonsOfMugloarApi.solveMessage(gameId, getMessage(gameId));
            currentScore = solutionResponse.score();

            if (solutionResponse.lives() == 0) {
                break;
            }

            if (solutionResponse.lives() == 1) {
                var items = dragonsOfMugloarApi.listItemsAvailableInShop(gameId);
                var affordableItems = items.stream().filter(t -> t.cost() < solutionResponse.gold()).toList();
                var availableHpPotions = items.stream().filter(t -> t.id().equals(HEALTH_POT_ID) && t.cost() < solutionResponse.gold()).toList();
                if (availableHpPotions.size() > 0) {
                    dragonsOfMugloarApi.purchaseShopItem(gameId, availableHpPotions.stream().findAny().orElseThrow().id());
                } else if (affordableItems.size() > 0) {
                    dragonsOfMugloarApi.purchaseShopItem(gameId, affordableItems.stream().findAny().orElseThrow().id());
                }
            }
        }
        System.out.println("Final score: " + currentScore);

        return currentScore;
    }

    private String getMessage(String gameId) {
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

}
