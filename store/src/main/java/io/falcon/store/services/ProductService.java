package io.falcon.store.services;

import io.falcon.store.entities.Product;
import io.falcon.store.events.ProductsLoadedEvent;
import io.falcon.store.repositories.ProductRepository;
import io.falcon.store.repositories.projections.RequestedProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public Optional<Product> getByName(String name) {
        return repository.findByNameIgnoreCase(name);
    }

    public Product create(String name) {
        return repository.save(new Product(name));
    }

    public List<Product> save(List<Product> products) {
        return (List<Product>) repository.saveAll(products);
    }

    public Page<Product> getPage(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<Product> addProducts(List<Product> products) {

        List<Product> newAndUpdatedProducts = products.stream()
                .map(product -> getByName(product.getName())
                        .map(existingProduct -> existingProduct.addQuantity(product.getQuantity()))
                        .orElse(product))
                .collect(Collectors.toList());

        newAndUpdatedProducts = save(newAndUpdatedProducts);
        eventPublisher.publishEvent(new ProductsLoadedEvent(newAndUpdatedProducts));

        return newAndUpdatedProducts;
    }

    public List<RequestedProduct> getRequestedProducts() {
        return repository.getRequestedProducts();
    }
}
