package ru.tishembitov.orderservice.controller;

import ru.tishembitov.orderservice.config.ContextHolder;
import ru.tishembitov.orderservice.config.RequiresAuthentication;
import ru.tishembitov.orderservice.dto.OrderCreateDto;
import ru.tishembitov.orderservice.dto.OrderDto;
import ru.tishembitov.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = {"api/v1/orders"})
@Tag(name = "Order")
public class OrderController {

    private final OrderService orderService;
    private final ContextHolder contextHolder;

    /**
     * Create a new order
     *
     * @param request        The HttpServletRequest.
     * @param orderCreateDto The order to create.
     * @return The created order.
     */
    @Operation(summary = "Create a new order",
               responses = {
                       @ApiResponse(responseCode = "200",
                                    content = {
                                            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                     schema = @Schema(implementation = OrderDto.class))
                                    }),
                       @ApiResponse(responseCode = "400",
                                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                       schema = @Schema(implementation = ProblemDetail.class)))
               },
               security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("users/me")
    @RequiresAuthentication
    public ResponseEntity<OrderDto> add(
            HttpServletRequest request,
            @Valid
            @RequestBody
            OrderCreateDto orderCreateDto
    ) {
        this.logRequest(request, orderCreateDto);

        var orderDto = this.orderService.addOne(orderCreateDto);

        return ResponseEntity.ok(orderDto);
    }

    /**
     * Get an order for the current user
     *
     * @param request The HttpServletRequest.
     * @param orderId The order id.
     * @return The order.
     */
    @Operation(summary = "Get an order for the current user",
               responses = {
                       @ApiResponse(responseCode = "200",
                                    content = {
                                            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                     schema = @Schema(implementation = OrderDto.class))
                                    }),
                       @ApiResponse(responseCode = "400",
                                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                       schema = @Schema(implementation = ProblemDetail.class)))
               },
               security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("users/me/{orderId}")
    @RequiresAuthentication
    public ResponseEntity<OrderDto> getOne(
            HttpServletRequest request,
            @PathVariable
            Long orderId
    ) {
        this.logRequest(request);

        var orderDto = this.orderService.getOne(orderId);

        return ResponseEntity.ok(orderDto);
    }

    private void logRequest(
            final HttpServletRequest request,
            final Object obj
    ) {
        log.info(
                "{} - {} - {} - {} - {}",
                request.getMethod(),
                request.getRequestURI(),
                this.contextHolder.getCorrelationId(),
                this.contextHolder.getUsername(),
                obj
        );
    }

    private void logRequest(
            final HttpServletRequest request
    ) {
        log.info(
                "{} - {} - {} - {} - {}",
                request.getMethod(),
                request.getRequestURI(),
                this.contextHolder.getCorrelationId(),
                this.contextHolder.getUsername(),
                null
        );
    }
}
