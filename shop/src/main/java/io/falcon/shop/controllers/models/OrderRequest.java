package io.falcon.shop.controllers.models;

import io.falcon.shop.entities.Order;
import io.falcon.shop.entities.Product;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Setter
public class OrderRequest {

    @NotEmpty(message = "Products list cannot be empty")
    private List<ProductRequest> products;

    public Order toEntity() {
        List<Product> productEntities = products.stream()
                .map(ProductRequest::toEntity)
                .collect(Collectors.toList());

        return new Order(productEntities);
    }

    @Setter
    private static class ProductRequest {

        @NotBlank(message = "Product name cannot be blank")
        private String name;

        @NotNull(message = "Product quantity cannot be empty")
        private Integer quantity;

        private Product toEntity() {
            return new Product(name, quantity);
        }
    }
}
