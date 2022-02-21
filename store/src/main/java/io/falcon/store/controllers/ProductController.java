package io.falcon.store.controllers;

import io.falcon.store.models.ProductRequest;
import io.falcon.store.models.ProductResponse;
import io.falcon.store.entities.Product;
import io.falcon.store.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService service;

    @GetMapping
    public Page<ProductResponse> getProducts(Pageable pageable) {
        return service.getPage(pageable)
                .map(ProductResponse::new);
    }

    @PostMapping
    public List<ProductResponse> addProducts(@RequestBody @Valid List<ProductRequest> productsRequest) {
        List<Product> products = productsRequest.stream()
                .map(ProductRequest::toEntity)
                .collect(Collectors.toList());

        products = service.addProducts(products);

        return products.stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
    }
}
