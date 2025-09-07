package ru.tishembitov.inventoryservice.repository;

import org.springframework.stereotype.Repository;
import ru.tishembitov.inventoryservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ProductRepository
        extends JpaRepository<Product, Long> {
    boolean existsByName(String name);


}