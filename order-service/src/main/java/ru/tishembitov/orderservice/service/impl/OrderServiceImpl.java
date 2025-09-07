package ru.tishembitov.orderservice.service.impl;

import ru.tishembitov.orderservice.config.ContextHolder;
import ru.tishembitov.orderservice.exception.OrderException;
import ru.tishembitov.orderservice.dto.OrderCreateDto;
import ru.tishembitov.orderservice.dto.OrderDto;
import ru.tishembitov.orderservice.dto.OrderProductCreateDto;
import ru.tishembitov.orderservice.dto.ProductDto;
import ru.tishembitov.orderservice.kafka.OrderEventProducer;
import ru.tishembitov.orderservice.mapper.OrderMapper;
import ru.tishembitov.orderservice.entity.Order;
import ru.tishembitov.orderservice.entity.OrderProduct;
import ru.tishembitov.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tishembitov.orderservice.service.OrderService;
import ru.tishembitov.orderservice.service.ProductServiceClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductServiceClient productServiceClient;
    private final OrderEventProducer orderEventProducer;
    private final ContextHolder contextHolder;


    @Transactional
    @Override
    public OrderDto addOne(final OrderCreateDto orderCreateDto) {

        var order = new Order();

        var productsOrder = this.getOrderProducts(orderCreateDto, order);
        order.setUserId(this.contextHolder.getUserId());
        order.setProducts(productsOrder);

        var orderSaved = this.orderRepository.save(order);

        var hasStockUpdated = this.productServiceClient.updateStock(orderCreateDto.products());

        if (!hasStockUpdated) {
            throw new OrderException(OrderException.ERROR_UPDATE_STOCK);
        }

        var dto = this.orderMapper.toDto(orderSaved);

        this.orderEventProducer.sendOrderCreate(dto);

        return dto;
    }

    @Override
    public OrderDto getOne(final Long orderId) {
        var order = this.orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(OrderException.ORDER_DOES_NOT_EXIST));

        return this.orderMapper.toDto(order);
    }

    private List<OrderProduct> getOrderProducts(
            final OrderCreateDto orderCreateDto,
            final Order order
    ) {

        var productsId = orderCreateDto.products()
                .stream()
                .map(OrderProductCreateDto::productId)
                .toList();

        var productById = this.productServiceClient.getProductById(productsId);

        return orderCreateDto.products()
                .stream()
                .map(orderProductCreateDtoToOrderProduct(productById, order))
                .toList();
    }

    private static Function<OrderProductCreateDto, OrderProduct> orderProductCreateDtoToOrderProduct(
            final Map<Long, ProductDto> productById,
            final Order order
    ) {
        return orderProductCreateDto -> {
            var productDto = Optional.ofNullable(productById.get(orderProductCreateDto.productId()))
                    .orElseThrow(() -> new OrderException(OrderException.PRODUCT_DOES_NOT_EXIST));

            if (orderProductCreateDto.quantity() > productDto.quantity()) {
                throw new OrderException(OrderException.STOCK_NOT_AVAILABLE);
            }

            return OrderProduct.builder()
                    .price(productDto.price())
                    .productId(orderProductCreateDto.productId())
                    .quantity(orderProductCreateDto.quantity())
                    .order(order)
                    .build();
        };
    }
}