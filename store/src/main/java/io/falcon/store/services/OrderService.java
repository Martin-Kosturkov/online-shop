package io.falcon.store.services;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @KafkaListener(topics = "orders")
    public void acceptOrder(String event) {
    }
}
