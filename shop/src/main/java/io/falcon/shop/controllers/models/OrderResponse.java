package io.falcon.shop.controllers.models;

import lombok.Getter;

@Getter
public class OrderResponse {

    private final String orderId;
    private final String message;

    public OrderResponse(String orderId, String message) {
        this.orderId = orderId;
        this.message = message;
    }
}
