package ru.tishembitov.inventoryservice.repository;

import ru.tishembitov.inventoryservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository
        extends JpaRepository<Product, Long> {
    boolean existsByName(String name);


}