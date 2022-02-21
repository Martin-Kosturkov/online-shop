package io.falcon.store.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.falcon.store.entities.OrderedProduct;
import io.falcon.store.entities.Product;
import io.falcon.store.events.CompleteOrderEvent;
import io.falcon.store.events.CheckAvailableProductsForPendingOrdersEvent;
import io.falcon.store.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderProcessor {

    private final OrderRepository repository;
    private final ProductService productService;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Async
    @EventListener
    public void checkProductsAvailable(CheckAvailableProductsForPendingOrdersEvent event) {
        var productNames = event.getProducts().stream()
                .map(Product::getName)
                .collect(Collectors.toList());

        var pendingOrders = repository.findByProductsProductNameInOrderByCreatedAt(productNames);

        pendingOrders.forEach(order -> {
            boolean hasEnoughProductsInStock = order.getProducts().stream()
                    .allMatch(orderedProduct ->
                            orderedProduct.getQuantity() <= orderedProduct.getProduct().getQuantity());

            if (hasEnoughProductsInStock) {
                removeProductsFromStock(order.getProducts());
                eventPublisher.publishEvent(new CompleteOrderEvent(order));
            }
        });
    }

    private void removeProductsFromStock(List<OrderedProduct> orderedProducts) {
        var products = orderedProducts.stream()
                .peek(orderedProduct -> orderedProduct.getProduct().decreaseQuantity(orderedProduct.getQuantity()))
                .map(OrderedProduct::getProduct)
                .collect(Collectors.toList());

        productService.save(products);
    }

    @Async
    @EventListener
    public void removeCompletedOrder(CompleteOrderEvent event) {
        repository.delete(event.getOrder());
    }

    @SneakyThrows(JsonProcessingException.class)
    @Async
    @EventListener
    public void sendResultToShop(CompleteOrderEvent event) {
        var order = event.getOrder();
        var bodyMap = Map.of(
                "id", order.getId().toString(),
                "status", "SUCCESS");

        var bodyJsonString = objectMapper.writeValueAsString(bodyMap);

        kafkaTemplate.send("order-result", bodyJsonString).completable()
                .whenComplete((__, exception) -> {
                    // TODO log exception cause if any
                });
    }
}
