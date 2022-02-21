package io.falcon.store.models;

import io.falcon.store.entities.Product;
import lombok.Getter;

@Getter
public class ProductResponse {

    private final String name;
    private final Integer quantity;

    public ProductResponse(Product product) {
        name = product.getName();
        quantity = product.getQuantity();
    }
}
