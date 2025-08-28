package ru.tishembitov.inventoryservice.service;

import ru.tishembitov.inventoryservice.dto.ProductCreateDto;
import ru.tishembitov.inventoryservice.dto.ProductDto;
import ru.tishembitov.inventoryservice.dto.ProductStockQuantityDto;

import java.util.List;

public interface ProductService {

    ProductDto addOne(ProductCreateDto productCreateDto);

    List<ProductDto> getAll();

    void increaseStock(List<ProductStockQuantityDto> productsQuantities);

    void decreaseStock(List<ProductStockQuantityDto> productsQuantities);

    ProductDto getOneById(Long id);

    List<ProductDto> getAllByIds(List<Long> ids);
}
