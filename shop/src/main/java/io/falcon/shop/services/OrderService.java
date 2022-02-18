package io.falcon.shop.services;

import io.falcon.shop.entities.Order;
import io.falcon.shop.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;

    public String save(Order order) {
        return repository.save(order).getIdAsString();
    }
}
