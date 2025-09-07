package ru.tishembitov.reviewsservice.service;

import ru.tishembitov.reviewsservice.dto.ProductAvgRatDto;
import ru.tishembitov.reviewsservice.dto.ReviewCreateDto;
import ru.tishembitov.reviewsservice.dto.ReviewDto;

import java.util.List;

public interface ReviewService {
    ReviewDto addOne(final ReviewCreateDto reviewCreateDto);

    List<ReviewDto> getAllByProduct(final Long productId);

    List<ReviewDto> getAllByUser();

    boolean deleteOne(final Long reviewId);

    List<ProductAvgRatDto> getTopAvgRatedProducts(int max);
}
