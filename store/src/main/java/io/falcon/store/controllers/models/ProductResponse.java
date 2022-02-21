package io.falcon.store.controllers.models;

import io.falcon.store.entities.Product;
import io.falcon.store.repositories.projections.RequestedProduct;
import lombok.Getter;

@Getter
public class ProductResponse {

    private final String name;
    private final Integer quantity;

    public ProductResponse(Product product) {
        name = product.getName();
        quantity = product.getQuantity();
    }

    public ProductResponse(RequestedProduct product) {
        this.name = product.getName();
        this.quantity = product.getRequestedQuantity();
    }
}
