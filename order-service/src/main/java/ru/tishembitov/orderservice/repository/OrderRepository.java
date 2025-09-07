package ru.tishembitov.orderservice.repository;

import org.springframework.stereotype.Repository;
import ru.tishembitov.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface OrderRepository
        extends JpaRepository<Order, Long> {
}