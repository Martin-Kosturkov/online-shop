package io.falcon.store.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.falcon.store.entities.OrderedProduct;
import io.falcon.store.entities.PendingOrder;
import io.falcon.store.events.CheckProductsAvailableForPendingOrdersEvent;
import io.falcon.store.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderReceiverService {

    private final OrderRepository repository;
    private final ProductService productService;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

    @KafkaListener(topics = "orders")
    public void processOrder(String orderEvent) {
        var orderDto = parseEvent(orderEvent);
        var orderedProducts = mapProducts(orderDto.products);

        var pendingOrder = new PendingOrder(orderDto.id, orderDto.createdAt, orderedProducts);
        repository.save(pendingOrder);

        var products = orderedProducts.stream()
                .map(OrderedProduct::getProduct)
                .collect(Collectors.toList());

        eventPublisher.publishEvent(new CheckProductsAvailableForPendingOrdersEvent(products));
    }

    private List<OrderedProduct> mapProducts(List<ProductDto> products) {
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
