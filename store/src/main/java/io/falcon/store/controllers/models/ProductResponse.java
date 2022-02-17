package io.falcon.store.controllers.models;

import io.falcon.store.entities.Product;
import lombok.Getter;

@Getter
public class ProductResponse {

    private final String name;
    private final Integer quantity;

    public ProductResponse(Product product) {
        this.name = product.getName();
        this.quantity = product.getQuantity();
    }
}
