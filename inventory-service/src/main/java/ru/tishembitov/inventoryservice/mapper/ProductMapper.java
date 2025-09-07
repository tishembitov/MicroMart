package ru.tishembitov.inventoryservice.mapper;

import ru.tishembitov.inventoryservice.dto.ProductCreateDto;
import ru.tishembitov.inventoryservice.dto.ProductDto;
import ru.tishembitov.inventoryservice.dto.ProductStockQuantityDto;
import ru.tishembitov.inventoryservice.entity.Product;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {
    Product toProduct(ProductCreateDto productCreateDto);

    ProductDto toDto(Product product);

    List<ProductDto> toDto(List<Product> products);

    void partialUpdate(
            ProductCreateDto productCreateDto,
            @MappingTarget
            Product product
    );

    Product toEntity(ProductStockQuantityDto productStockQuantityDto);

    ProductStockQuantityDto toDto1(Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Product partialUpdate(
            ProductStockQuantityDto productStockQuantityDto,
            @MappingTarget
            Product product
    );
}


