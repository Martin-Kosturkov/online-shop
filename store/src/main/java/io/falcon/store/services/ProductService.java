package io.falcon.store.services;

import io.falcon.store.entities.Product;
import io.falcon.store.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    public Page<Product> getPage(Pageable pageable) {
        return repository.findAll(pageable);
    }
}
