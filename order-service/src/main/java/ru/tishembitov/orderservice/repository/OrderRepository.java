package ru.tishembitov.orderservice.repository;

import ru.tishembitov.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository
        extends JpaRepository<Order, Long> {
}