package io.falcon.shop.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.falcon.shop.entities.Order;
import io.falcon.shop.entities.Product;
import io.falcon.shop.event.OrderStatusChangedEvent;
import io.falcon.shop.repositories.OrderRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

    public String save(Order order) {
        order = repository.save(order);

        sendToStore(order);

        return order.getIdAsString();
    }

    @KafkaListener(topics = "order-result")
    public void acceptOrderResult(String orderResultEvent) {
        var orderResult = parse(orderResultEvent, OrderResult.class);
        var orderId = UUID.fromString(orderResult.id);

        var order = repository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order with id " + orderId + " was not found"));

        var newOrderStatus = orderResult.isSuccess()
                ? Order.Status.COMPLETED
                : Order.Status.CANCELLED;

        setOrderStatus(order, newOrderStatus);
    }

    @Async
    @EventListener
    public void orderStatusChanged(OrderStatusChangedEvent event) {
        repository.save(event.getOrder());
    }

    private void sendToStore(Order order) {
        var jsonBody = orderToString(order);

        kafkaTemplate.send("orders", jsonBody).completable()
                .whenComplete((__, exception) -> {
                    if (exception != null) {
                        // TODO log exception cause
                        setOrderStatus(order, Order.Status.CANCELLED);
                    }
                });
    }

    private String orderToString(Order order) {
        var body = new OrderDto(order);

        try {
            return objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            setOrderStatus(order, Order.Status.CANCELLED);
            throw new RuntimeException(e);
        }
    }

    private <T> T parse(String jsonString, Class<T> type) {
        try {
            return objectMapper.readValue(jsonString, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void setOrderStatus(Order order, Order.Status newStatus) {
        order.setStatus(newStatus);
        eventPublisher.publishEvent(new OrderStatusChangedEvent(order));
    }

    @Getter
    private static class OrderDto {

        private final String id;
        private final Instant createdAt;
        private final List<Map<String, Object>> products;

        public OrderDto(Order order) {
            id = order.getIdAsString();
            createdAt = order.getCreatedAt();
            products = order.getProducts().stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
        }

        private Map<String, Object> toDto(Product product) {
            return Map.of(
                    "name", product.getName(),
                    "quantity", product.getQuantity());
        }
    }

    @Setter
    private static class OrderResult {
        private String id;
        private String status;

        public boolean isSuccess() {
            return "SUCCESS".equals(status);
        }
    }
}
