package io.falcon.store.repositories;

import io.falcon.store.entities.PendingOrder;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface OrderRepository extends PagingAndSortingRepository<PendingOrder, UUID> {

    Set<PendingOrder> findByProductsProductNameInOrderByCreatedAt(List<String> productNames);
}
