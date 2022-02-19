package io.falcon.shop.entities;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "customer_order")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Order {

    @Id
    private UUID id;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "last_updated_at", nullable = false)
    private Instant lastUpdatedAt;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "order_id", nullable = false)
    private List<Product> products;

    public Order(List<Product> products) {
        if (products == null || products.size() == 0) {
            throw new IllegalArgumentException("Products list cannot be empty");
        }

        this.id = UUID.randomUUID();
        this.status = Status.ACCEPTED;
        this.lastUpdatedAt = Instant.now();
        this.products = new ArrayList<>(products);
    }

    public String getIdAsString() {
        return id.toString();
    }

    public List<Product> getProducts() {
        return List.copyOf(products);
    }

    public void cancel() {
        status = Status.CANCELLED;
    }

    private enum Status {
        ACCEPTED,
        COMPLETED,
        CANCELLED
    }
}
