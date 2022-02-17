package io.falcon.store.entities;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "product")
@Getter
public class Product {

    @Id
    private String name;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}
