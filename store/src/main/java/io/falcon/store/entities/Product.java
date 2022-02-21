package io.falcon.store.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Optional;
import java.util.function.Predicate;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Product {

    @Id
    private String name;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    public Product(String name) {
        this(name, 0);
    }

    public Product(String name, Integer quantity) {
        this.name = Optional.ofNullable(name)
                .filter(Predicate.not(String::isBlank))
                .orElseThrow(() -> new IllegalArgumentException("Product name cannot be empty"));

        this.quantity = Optional.ofNullable(quantity)
                .filter(q -> q >= 0)
                .orElseThrow(() -> new IllegalArgumentException("Product quantity cannot be less than 0"));
    }

    public Product addQuantity(Integer quantity) {
        this.quantity += quantity;
        return this;
    }

    public Product decreaseQuantity(Integer quantity) {
        this.quantity -= quantity;
        return this;
    }
}
