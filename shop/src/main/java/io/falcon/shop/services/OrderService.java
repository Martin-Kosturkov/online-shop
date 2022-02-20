package io.falcon.shop.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.falcon.shop.entities.Order;
import io.falcon.shop.entities.Product;
import io.falcon.shop.repositories.OrderRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public String save(Order order) {
        order = repository.save(order);

        sendToStore(order);

        return order.getIdAsString();
    }

    private void sendToStore(Order order) {
        var jsonBody = orderToString(order);

        kafkaTemplate.send("orders", jsonBody).completable()
                .whenComplete((__, exception) -> {
                    if (exception != null) {
                        // TODO log exception cause
                        cancelOrder(order);
                    }
                });
    }

    private String orderToString(Order order) {
        var body = new OrderDto(order);

        try {
            return objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            // TODO add logging
            cancelOrder(order);
            throw new RuntimeException(e);
        }
    }

    private void cancelOrder(Order order) {
        order.cancel();
        repository.save(order);
    }

    @Getter
    private static class OrderDto {

        private final String id;
        private final List<Map<String, Object>> products;

        public OrderDto(Order order) {
            id = order.getIdAsString();
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
}
