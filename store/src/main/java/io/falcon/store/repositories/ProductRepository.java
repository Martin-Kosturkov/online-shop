package io.falcon.store.repositories;

import io.falcon.store.entities.Product;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface ProductRepository extends PagingAndSortingRepository<Product, String> {

    Optional<Product> findByNameIgnoreCase(String name);

}
