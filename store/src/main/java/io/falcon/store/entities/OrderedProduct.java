package io.falcon.store.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Optional;

@Entity
@Table(name = "ordered_product")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class OrderedProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_name", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    public OrderedProduct(Product product, Integer quantity) {
        this.product = Optional.ofNullable(product)
                .orElseThrow(() -> new IllegalArgumentException("Product cannot be null"));

        this.quantity = Optional.ofNullable(quantity)
                .filter(q -> q >= 1)
                .orElseThrow(() -> new IllegalArgumentException("Quantity cannot be less than 1"));
    }
}
