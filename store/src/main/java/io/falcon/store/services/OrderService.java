package io.falcon.store.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.falcon.store.entities.OrderedProduct;
import io.falcon.store.entities.PendingOrder;
import io.falcon.store.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final ProductService productService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "orders")
    public void saveNewOrder(String event) {
        var orderDto = parseEvent(event);
        var products = findProducts(orderDto.products);

        var pendingOrder = new PendingOrder(orderDto.id, orderDto.createdAt, products);
        repository.save(pendingOrder);
    }

    private List<OrderedProduct> findProducts(List<ProductDto> products) {
        return products.stream()
                .map(productDto -> {
                    var product = productService.getByName(productDto.name)
                            .orElseGet(() -> productService.create(productDto.name));

                    return new OrderedProduct(product, productDto.quantity);
                })
                .collect(Collectors.toList());
    }

    private OrderDto parseEvent(String event) {
        try {
            return objectMapper.readValue(event, OrderDto.class);
        } catch (JsonProcessingException e) {
            // TODO add logging
            // TODO cancell order
            throw new RuntimeException(e);
        }
    }

    @Setter
    private static class OrderDto {
        private String id;
        private Instant createdAt;
        List<ProductDto> products;
    }

    @Setter
    private static class ProductDto {
        private String name;
        private Integer quantity;
    }
}
