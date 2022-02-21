package io.falcon.shop.services;

import io.falcon.shop.event.OrderStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderStatusNotifier {

    private final SimpMessagingTemplate messagingTemplate;

    @Async
    @EventListener
    public void notifyOrderStatusChange(OrderStatusChangedEvent event) {
        var order = event.getOrder();
        var body = Map.of(
                "id", order.getIdAsString(),
                "status", order.getStatus().toString());

        messagingTemplate.convertAndSend("/order-status", body);
    }
}
