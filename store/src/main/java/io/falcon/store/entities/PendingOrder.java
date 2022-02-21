package io.falcon.store.entities;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "pending_order")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PendingOrder {

    @Id
    private UUID id;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "order_id", nullable = false)
    private List<OrderedProduct> products;

    public PendingOrder(String id, Instant createdAt, List<OrderedProduct> products) {
        this.id = Optional.ofNullable(id)
                .map(UUID::fromString)
                .orElseThrow(() -> new IllegalArgumentException("Id cannot be null"));

        this.createdAt = Optional.ofNullable(createdAt)
                .orElseThrow(() -> new IllegalArgumentException("Order creation time must be specified"));

        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("At least one product mus tbe provided");
        }

        this.products = new ArrayList<>(products);
    }

    public List<OrderedProduct> getProducts() {
        return List.copyOf(products);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PendingOrder order = (PendingOrder) o;
        return id != null && Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
