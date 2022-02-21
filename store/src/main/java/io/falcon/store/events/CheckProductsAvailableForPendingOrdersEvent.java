package io.falcon.store.events;

import io.falcon.store.entities.Product;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class CheckProductsAvailableForPendingOrdersEvent extends ApplicationEvent {

    private final List<Product> products;

    public CheckProductsAvailableForPendingOrdersEvent(List<Product> products) {
        super(products);
        this.products = products;
    }
}
