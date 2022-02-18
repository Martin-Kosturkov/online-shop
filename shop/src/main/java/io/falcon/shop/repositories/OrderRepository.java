package io.falcon.shop.repositories;

import io.falcon.shop.entities.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface OrderRepository extends CrudRepository<Order, UUID> {

}
