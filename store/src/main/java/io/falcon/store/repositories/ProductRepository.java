package io.falcon.store.repositories;

import io.falcon.store.entities.Product;
import io.falcon.store.repositories.projections.RequestedProduct;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends PagingAndSortingRepository<Product, String> {

    Optional<Product> findByNameIgnoreCase(String name);

    @Query(value =
            "select op.product_name as name, (op.requestedProducts - p.quantity) as requestedQuantity " +
            "from (" +
            "         select op.product_name, sum(op.quantity) requestedProducts " +
            "         from ordered_product op " +
            "         group by op.product_name) op " +
            "         join product p on op.product_name = p.name " +
            "where op.requestedProducts - p.quantity > 0",
            nativeQuery = true)
    List<RequestedProduct> getRequestedProducts();
}
