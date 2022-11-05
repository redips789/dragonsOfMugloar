package com.example.rest;

public record Message(String adId, String message, Integer reward, Integer expiresIn, String probability) {

    public static MessageWithCategory mapTo(Message message) {
        return new MessageWithCategory(message.adId(), message.reward(), MissionDifficulty.getCategory(message.probability()));
    }


}
