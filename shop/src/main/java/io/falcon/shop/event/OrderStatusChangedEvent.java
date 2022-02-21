package io.falcon.shop.event;

import io.falcon.shop.entities.Order;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OrderStatusChangedEvent extends ApplicationEvent {

    private final Order order;

    public OrderStatusChangedEvent(Order order) {
        super(order);
        this.order = order;
    }
}
