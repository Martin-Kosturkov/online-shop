package io.falcon.store.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "product")
public class Product {

    @Id
    private String name;

    @Column(name = "quantity", nullable = false)
    private String quantity;
}
