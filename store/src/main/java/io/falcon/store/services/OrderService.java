package io.falcon.store.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.falcon.store.entities.OrderedProduct;
import io.falcon.store.entities.PendingOrder;
import io.falcon.store.entities.Product;
import io.falcon.store.events.CompleteOrderEvent;
import io.falcon.store.events.ProductsLoadedEvent;
import io.falcon.store.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
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
    private final ApplicationEventPublisher eventPublisher;

    @KafkaListener(topics = "orders")
    public void saveNewOrder(String event) {
        var orderDto = parseEvent(event);
        var products = mapProducts(orderDto.products);

        var pendingOrder = new PendingOrder(orderDto.id, orderDto.createdAt, products);
        repository.save(pendingOrder);
    }

    /**
     * Check whether an order containing any of the loaded products can be completed.
     * If yes, complete it.
     *
     * @param event containing products that have been loaded in store
     */
    @Async
    @EventListener
    public void handleProductsLoaded(ProductsLoadedEvent event) {
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
        List<Product> products = orderedProducts.stream()
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

    @Async
    @EventListener
    public void sendResultToShop(CompleteOrderEvent event) {

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
