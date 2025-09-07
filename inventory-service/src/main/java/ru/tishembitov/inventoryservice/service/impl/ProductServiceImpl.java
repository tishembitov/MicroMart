package ru.tishembitov.inventoryservice.service.impl;

import org.springframework.transaction.annotation.Transactional;
import ru.tishembitov.inventoryservice.exception.ProductException;
import ru.tishembitov.inventoryservice.mapper.ProductMapper;
import ru.tishembitov.inventoryservice.dto.ProductCreateDto;
import ru.tishembitov.inventoryservice.dto.ProductDto;
import ru.tishembitov.inventoryservice.dto.ProductStockQuantityDto;
import ru.tishembitov.inventoryservice.entity.Product;
import ru.tishembitov.inventoryservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tishembitov.inventoryservice.service.ProductService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ProductDto addOne(final ProductCreateDto productCreateDto) {

        var exists = this.productRepository.existsByName(productCreateDto.name());

        if (exists) {
            throw new ProductException(ProductException.ALREADY_EXISTS_PRODUCT);
        }

        var product = this.productMapper.toProduct(productCreateDto);

        var productSaved = this.productRepository.save(product);

        return this.productMapper.toDto(productSaved);
    }

    @Override
    public List<ProductDto> getAll() {
        var productsList = this.productRepository.findAll();

        return this.productMapper.toDto(productsList);
    }

    @Override
    public ProductDto getOneById(final Long id) {
        var product = this.productRepository.findById(id)
                                            .orElseThrow(() -> new ProductException(ProductException.PRODUCT_DOES_NOT_EXIST));

        return this.productMapper.toDto(product);
    }

    @Override
    public List<ProductDto> getAllByIds(final List<Long> ids) {
        var products = this.productRepository.findAllById(ids);
        return this.productMapper.toDto(products);
    }

    @Override
    @Transactional
    public void increaseStock(final List<ProductStockQuantityDto> productsQuantities) {

        var productsMap = this.createProductsMapFromQuantities(productsQuantities);

        productsQuantities
                .forEach(productQuantity -> {
                    var product = productsMap.get(productQuantity.productId());
                    var newQuantity = product.getQuantity() + productQuantity.quantity();

                    product.setQuantity(newQuantity);
                });

        var updatedProducts = productsMap.values()
                                         .stream()
                                         .toList();

        this.productRepository.saveAll(updatedProducts);
    }

    @Override
    @Transactional
    public void decreaseStock(final List<ProductStockQuantityDto> productsQuantities) {

        var productsMap = this.createProductsMapFromQuantities(productsQuantities);

        productsQuantities
                .forEach(productQuantity -> {
                    var product = productsMap.get(productQuantity.productId());
                    var newQuantity = product.getQuantity() - productQuantity.quantity();

                    if (newQuantity < 0) {
                        throw new ProductException(ProductException.QUANTITY_LOWER_ZERO + ", " + product.getName() + ", " + product.getQuantity());
                    }

                    product.setQuantity(newQuantity);
                });

        var updatedProducts = productsMap.values()
                                         .stream()
                                         .toList();

        this.productRepository.saveAll(updatedProducts);
    }

    private Map<Long, Product> createProductsMapFromQuantities(final List<ProductStockQuantityDto> productsQuantities) {
        var productIds = productsQuantities.stream()
                                           .map(ProductStockQuantityDto::productId)
                                           .toList();

        var productsMap = this.createProductsMap(productIds);

        if (productsMap.size() != productIds.size()) {
            throw new ProductException(ProductException.PRODUCT_DOES_NOT_EXIST);
        }
        return productsMap;
    }

    private Map<Long, Product> createProductsMap(final List<Long> productIds) {
        return this.productRepository.findAllById(productIds)
                                     .stream()
                                     .collect(Collectors.toMap(Product::getId, Function.identity()));
    }
}
