package io.falcon.shop.controllers;

import io.falcon.shop.controllers.models.OrderRequest;
import io.falcon.shop.controllers.models.OrderResponse;
import io.falcon.shop.entities.Order;
import io.falcon.shop.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/orders")
@Validated
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid OrderRequest orderRequest) {
        var order = orderRequest.toEntity();
        var orderId = orderService.save(order);

        var response = new OrderResponse(orderId, "The order has been submitted");

        return ResponseEntity.accepted()
                .body(response);
    }
}
