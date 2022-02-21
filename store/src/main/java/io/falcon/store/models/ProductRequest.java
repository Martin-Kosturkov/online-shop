package io.falcon.store.models;

import io.falcon.store.entities.Product;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
public class ProductRequest {

    @NotBlank
    private String name;

    @NotNull
    private Integer quantity;

    public Product toEntity() {
        return new Product(name, quantity);
    }
}
