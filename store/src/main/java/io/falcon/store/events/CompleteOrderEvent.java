package io.falcon.store.events;

import io.falcon.store.entities.PendingOrder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CompleteOrderEvent extends ApplicationEvent {

    private final PendingOrder order;

    public CompleteOrderEvent(PendingOrder order) {
        super(order);
        this.order = order;
    }
}
