package io.falcon.store.services;

import io.falcon.store.entities.Product;
import io.falcon.store.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    public Page<Product> getPage(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<Product> addProducts(List<Product> products) {

        List<Product> newAndUpdatedProducts = products.stream()
                .map(product -> repository.findById(product.getName())
                        .map(existingProduct -> existingProduct.addQuantity(product.getQuantity()))
                        .orElse(product))
                .collect(Collectors.toList());

        return (List<Product>) repository.saveAll(newAndUpdatedProducts);
    }
}
