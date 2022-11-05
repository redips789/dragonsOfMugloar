package com.dragons.model;

public record PurchaseItemResponse(Boolean shoppingSuccess,
                                   Integer gold,
                                   Integer lives,
                                   Integer level,
                                   Integer turn) {
}
