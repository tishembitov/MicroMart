package ru.tishembitov.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;


import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public record OrderCreateDto(
        List<OrderProductCreateDto> products
)
{
}