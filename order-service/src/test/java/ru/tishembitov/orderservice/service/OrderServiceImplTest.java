package ru.tishembitov.orderservice.service;

import ru.tishembitov.orderservice.config.ContextHolder;
import ru.tishembitov.orderservice.exception.OrderException;
import ru.tishembitov.orderservice.dto.OrderCreateDto;
import ru.tishembitov.orderservice.dto.OrderDto;
import ru.tishembitov.orderservice.dto.OrderProductCreateDto;
import ru.tishembitov.orderservice.dto.ProductDto;
import ru.tishembitov.orderservice.entity.Order;
import ru.tishembitov.orderservice.kafka.OrderEventProducer;
import ru.tishembitov.orderservice.mapper.OrderMapper;
import ru.tishembitov.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tishembitov.orderservice.service.impl.OrderServiceImpl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    OrderRepository orderRepository;
    @Mock
    OrderMapper orderMapper;
    @Mock
    ProductServiceClient productServiceClient;
    @Mock
    OrderEventProducer orderEventProducer;
    @Mock
    ContextHolder contextHolder;
    @InjectMocks
    OrderServiceImpl orderServiceImpl;
    private ProductDto productDto;

    @BeforeEach
    void beforeEach() {

        this.productDto = ProductDto
                .builder()
                .id(1L)
                .name("Trackpad")
                .description("Apple Magic Trackpad")
                .price(BigDecimal.valueOf(99.99))
                .quantity(100)
                .build();

        lenient().when(this.contextHolder.getUserId())
                 .thenReturn(1L);
        lenient().when(this.contextHolder.getUsername())
                 .thenReturn("JhonDoe");

    }

    @Test
    void addOne_WhenValidOrder_ShouldCreateTheOrder() {
        //Arrange
        var orderProductCreate = OrderProductCreateDto
                .builder()
                .productId(this.productDto.id())
                .quantity(20)
                .build();

        var orderCreateDto = OrderCreateDto.builder()
                                           .products(List.of(orderProductCreate))
                                           .build();

        when(this.productServiceClient.getProductById(anyList()))
                .thenReturn(Map.of(this.productDto.id(), this.productDto));
        when(this.productServiceClient.updateStock(orderCreateDto.products()))
                .thenReturn(true);
        when(this.orderRepository.save(any(Order.class)))
                .thenReturn(new Order());
        when(this.orderMapper.toDto(any(Order.class)))
                .thenReturn(OrderDto.builder()
                                    .build());
        doNothing().when(this.orderEventProducer)
                   .sendOrderCreate(any(OrderDto.class));


        //Act
        var orderCreated = this.orderServiceImpl.addOne(orderCreateDto);

        //Assert
        Assertions.assertNotNull(orderCreated);
    }

    @Test
    void addOne_WhenProductDoesNotExist_ShouldThrowOrderException() {
        //Arrange
        var orderProductCreate = OrderProductCreateDto
                .builder()
                .productId(this.productDto.id())
                .quantity(this.productDto.quantity() + 1)
                .build();

        var orderCreateDto = OrderCreateDto.builder()
                                           .products(List.of(orderProductCreate))
                                           .build();

        when(this.productServiceClient.getProductById(anyList()))
                .thenReturn(Collections.emptyMap());

        //Act and Assert
        Assertions.assertThrows(
                OrderException.class,
                () -> this.orderServiceImpl.addOne(orderCreateDto),
                OrderException.PRODUCT_DOES_NOT_EXIST
        );
    }

    @Test
    void addOne_WhenProductDoesNotHaveStock_ShouldThrowOrderException() {
        //Arrange
        var orderProductCreate = OrderProductCreateDto
                .builder()
                .productId(this.productDto.id())
                .quantity(this.productDto.quantity() + 1)
                .build();

        var orderCreateDto = OrderCreateDto.builder()
                                           .products(List.of(orderProductCreate))
                                           .build();

        when(this.productServiceClient.getProductById(anyList()))
                .thenReturn(Map.of(this.productDto.id(), this.productDto));

        //Act and Assert
        Assertions.assertThrows(
                OrderException.class,
                () -> this.orderServiceImpl.addOne(orderCreateDto),
                OrderException.STOCK_NOT_AVAILABLE
        );
    }

    @Test
    void addOne_WhenStockNotUpdated_ShouldThrowOrderException() {
        //Arrange
        var orderProductCreate = OrderProductCreateDto
                .builder()
                .productId(this.productDto.id())
                .quantity(20)
                .build();

        var orderCreateDto = OrderCreateDto.builder()
                                           .products(List.of(orderProductCreate))
                                           .build();

        when(this.productServiceClient.getProductById(anyList()))
                .thenReturn(Map.of(this.productDto.id(), this.productDto));
        when(this.orderRepository.save(any(Order.class)))
                .thenReturn(new Order());
        when(this.productServiceClient.updateStock(orderCreateDto.products()))
                .thenReturn(false);

        //Act and Assert
        Assertions.assertThrows(
                OrderException.class,
                () -> this.orderServiceImpl.addOne(orderCreateDto),
                OrderException.ERROR_UPDATE_STOCK
        );
    }

    @Test
    void getOne_WhenOrderExists_ShouldReturnOrder() {
        //Arrange
        when(this.orderRepository.findById(anyLong()))
                .thenReturn(Optional.of(new Order()));
        when(this.orderMapper.toDto(any(Order.class)))
                .thenReturn(OrderDto.builder()
                                    .build());

        //Act
        var orderGet = this.orderServiceImpl.getOne(anyLong());

        //Assert
        Assertions.assertNotNull(orderGet);
    }

    @Test
    void getOne_WhenOrderDoesNotExists_ShouldReturnOrder() {
        //Arrange
        when(this.orderRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        //Act and Assert
        Assertions.assertThrows(
                OrderException.class,
                () -> this.orderServiceImpl.getOne(anyLong()),
                OrderException.ORDER_DOES_NOT_EXIST
        );
    }
}
