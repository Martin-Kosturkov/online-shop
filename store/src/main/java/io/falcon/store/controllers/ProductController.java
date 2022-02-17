package io.falcon.store.controllers;

import io.falcon.store.controllers.models.ProductResponse;
import io.falcon.store.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @GetMapping
    public Page<ProductResponse> getPage(Pageable pageable) {
        return service.getPage(pageable)
                .map(ProductResponse::new);
    }
}
